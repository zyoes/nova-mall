package com.example.user.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.core.UserContext;
import com.example.common.exception.CustomUnauthorizedException;
import com.example.common.exception.CustomValidationException;
import com.example.user.dto.request.UserPasswordRequest;
import com.example.user.dto.request.UserProfileRequest;
import com.example.user.dto.response.UserProfileResponse;
import com.example.user.entity.SysUser;
import com.example.user.mapper.SysUserMapper;
import com.example.user.service.AuthService;
import com.example.user.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserProfileService {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthService authService;

    @Override
    public UserProfileResponse getProfile() {
        // 获取当前登录用户ID
        Long id = UserContext.get();
        SysUser user = findUser(id);

        return BeanUtil.copyProperties(user, UserProfileResponse.class);
    }

    @Override
    @Transactional
    public void updateProfile(UserProfileRequest request) {
        // 获取当前登录用户ID
        Long id = UserContext.get();
        SysUser user = findUser(id);

        // 验证唯一性约束
        if (!user.getEmail().equals(request.getEmail())) {
            authService.ensureEmailNotRegistered(request.getEmail());
        }
        if (!user.getMobile().equals(request.getMobile())) {
            authService.ensureMobileNotRegistered(request.getMobile());
        }

        // 获取请求参数并复制到用户对象
        BeanUtil.copyProperties(request, user, "id");
        this.updateById(user);
    }

    @Override
    @Transactional
    public void updatePassword(UserPasswordRequest request) {
        // 获取当前登录用户ID
        Long id = UserContext.get();
        SysUser user = findUser(id);

        // 验证旧密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new CustomUnauthorizedException("旧密码错误");
        }

        // 更新密码
        String password = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(password);

        this.updateById(user);
    }

    // ==================== 逻辑抽取辅助方法 ====================
    private SysUser findUser(Long id) {
        return this.getOptById(id)
                .orElseThrow(() -> new CustomValidationException("用户不存在"));
    }
}
