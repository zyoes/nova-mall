package com.example.user.service.Impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.exception.CustomUnauthorizedException;
import com.example.common.exception.CustomValidationException;
import com.example.common.util.EmailUtil;
import com.example.user.dao.request.RegisterRequest;
import com.example.user.entity.SysUser;
import com.example.user.mapper.SysUserMapper;
import com.example.user.service.SysUserService;
import jakarta.annotation.Resource;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper,SysUser> implements SysUserService {
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
