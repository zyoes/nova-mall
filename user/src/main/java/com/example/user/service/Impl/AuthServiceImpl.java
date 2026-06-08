package com.example.user.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.exception.CustomUnauthorizedException;
import com.example.common.exception.CustomValidationException;
import com.example.user.dto.request.RegisterRequest;
import com.example.user.entity.SysUser;
import com.example.user.mapper.SysUserMapper;
import com.example.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 认证服务实现类
 */
@Service
public class AuthServiceImpl extends ServiceImpl<SysUserMapper,SysUser> implements AuthService {
    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * 用户登录认证
     * 根据邮箱查询用户，并验证密码是否匹配
     *
     * @param email    用户邮箱
     * @param password 用户密码（明文）
     * @return 认证通过的用户实体
     */
    @Override
    public SysUser authenticate(String email, String password) {
        QueryWrapper<SysUser> qw = new QueryWrapper<SysUser>().eq("email", email);
        SysUser sysUser = this.getOne(qw);

        if (sysUser == null) {
            throw new CustomUnauthorizedException("邮箱或密码错误");
        }

        if(!passwordEncoder.matches(password, sysUser.getPassword())) {
            throw new CustomUnauthorizedException("邮箱或密码错误");
        }

        return sysUser;
    }

    /**
     * 用户注册
     * 依次校验邮箱唯一性、手机号唯一性，然后创建用户并加密密码
     *
     * @param request 注册请求
     * @return 是否注册成功
     */
    @Override
    public boolean register(RegisterRequest request) {
        //1.验证邮箱是否已经注册过
        ensureEmailNotRegistered(request.getEmail());

        //2. 验证手机号是否被使用
        ensureMobileNotRegistered(request.getMobile());

        //3. 注册到数据库
        SysUser sysUser = new SysUser();
        sysUser.setEmail(request.getEmail());
        sysUser.setMobile(request.getMobile());
        sysUser.setPassword(passwordEncoder.encode(request.getPassword()));
        sysUser.setName(request.getName());

        return this.save(sysUser);
    }

    /**
     * 校验邮箱是否已被注册
     *
     * @param email 待校验邮箱
     * @throws CustomValidationException 邮箱已被注册时抛出
     */
    public void ensureEmailNotRegistered(String email){
        QueryWrapper<SysUser> qw = new QueryWrapper<>();
        qw.eq("email", email);
        Optional<SysUser> userOpt = this.getOneOpt(qw);
        if (userOpt.isPresent()){
            // 如果存在就是邮箱已被注册
            throw new CustomValidationException("邮箱已被注册", 400);
        }
    }

    /**
     * 校验手机号是否已被注册
     *
     * @param mobile 待校验手机号
     * @throws CustomValidationException 手机号已被使用时抛出
     */
    public void ensureMobileNotRegistered(String mobile){
        QueryWrapper<SysUser> qw = new QueryWrapper<>();
        qw.eq("mobile", mobile);
        Optional<SysUser> userOpt = this.getOneOpt(qw);
        if (userOpt.isPresent()){
            // 如果存在就是手机已被注册
            throw new CustomValidationException("手机号已被使用", 400);
        }
    }
}
