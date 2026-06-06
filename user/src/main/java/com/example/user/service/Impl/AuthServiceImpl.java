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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthServiceImpl extends ServiceImpl<SysUserMapper,SysUser> implements AuthService {
    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * 用户登录
     * @param email
     * @param password
     * @return
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
     * 注册用户信息
     * @param request
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
        sysUser.setPassword(new BCryptPasswordEncoder().encode(request.getPassword()));
        sysUser.setName(request.getName());
        sysUser.setCreatedAt(LocalDateTime.now());
        sysUser.setCreatedBy(sysUser.getId());

        return this.save(sysUser);
    }

    public void ensureEmailNotRegistered(String email){
        QueryWrapper<SysUser> qw = new QueryWrapper<>();
        qw.eq("email", email);
        Optional<SysUser> userOpt = this.getOneOpt(qw);
        if (userOpt.isPresent()){
            // 如果存在就是邮箱已被注册
            throw new CustomValidationException("邮箱已被注册", 400);
        }
    }

    public void ensureMobileNotRegistered(String mobile){
        QueryWrapper<SysUser> qw = new QueryWrapper<>();
        qw.eq("mobile", mobile);
        Optional<SysUser> userOpt = this.getOneOpt(qw);
        if (userOpt.isPresent()){
            // 如果存在就是邮箱已被注册
            throw new CustomValidationException("手机号已被使用", 400);
        }
    }
}
