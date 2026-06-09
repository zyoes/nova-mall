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

/**
 * 用户个人信息服务实现类
 */
@Service
public class UserProfileServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserProfileService {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthService authService;

    /**
     * 查看当前登录用户的个人信息
     *
     * @return 用户个人信息响应
     */
    @Override
    public UserProfileResponse getProfile() {
        // 获取当前登录用户ID
        Long id = UserContext.get();
        SysUser user = findUser(id);

        return BeanUtil.copyProperties(user, UserProfileResponse.class);
    }

    /**
     * 更新当前登录用户的个人信息
     * 若邮箱或手机号发生变更，会先校验新值的唯一性
     *
     * @param request 个人信息更新请求
     */
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

    /**
     * 修改当前登录用户的密码
     * 先验证旧密码是否正确，再加密保存新密码
     *
     * @param request 密码修改请求（包含旧密码和新密码）
     * @throws CustomUnauthorizedException 旧密码错误时抛出
     */
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
