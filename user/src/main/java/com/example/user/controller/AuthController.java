package com.example.user.controller;

import com.example.common.response.R;
import com.example.common.util.EmailUtil;
import com.example.common.util.JwtUtil;
import com.example.user.dao.request.LoginRequest;
import com.example.user.dao.request.RegisterRequest;
import com.example.user.dao.response.LoginResponse;
import com.example.user.entity.SysUser;
import com.example.user.service.EmailVerificationService;
import com.example.user.service.SysUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    SysUserService sysUserService;
    @Autowired
    EmailVerificationService emailVerificationService;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    EmailUtil emailUtil;

    @PostMapping("/register")
    public R<Object> register(@RequestBody @Valid RegisterRequest request) {
        //验证验证码是否正确
        emailVerificationService.validateAndConsumeRegisterCode(request.getEmail(),request.getVerifyCode());
        //注册成功
        boolean result = sysUserService.register(request);

        return new R<>(200,"注册成功",result);
    }

    @PostMapping("/send-code")
    public R<Object> sendEmailCode(@RequestBody Map<String, String> map) {
        //1.判断邮箱是否被注册过
        sysUserService.ensureEmailNotRegistered(map.get("email"));
        //2.验证码存储 验证码
        String code = emailVerificationService.generateAndSaveRegisterCode(map.get("email"));
        System.out.println(code);
        //2.发送验证码
        emailUtil.sendEmailCode(map.get("email"), code);

        return R.ok();
    }

    /**
     * 登录
     * @param request
     * @return
     */
    @PostMapping("/login")
    public R<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        //1. 获取前端传过来的密码 TODO: 解析前端的密文密码
        String password = request.getPassword();
        //2. 判断邮箱是否被注册
        sysUserService.ensureEmailNotRegistered(request.getEmail());
        //3. 判断邮箱和密码是否输入正确
        SysUser sysUser = sysUserService.authenticate(request.getEmail(), password);
        //4.生成令牌
        String token = jwtUtil.generateToken(sysUser.getId(), sysUser.getName());
        //5.封装 LoginResponse 类
        LoginResponse response = LoginResponse.builder().
                accessToken(token).
                userId(sysUser.getId().toString()).
                email(sysUser.getEmail()).build();

        return R.ok(response);
    }
}
