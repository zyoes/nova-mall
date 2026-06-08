package com.example.user.controller;

import com.example.common.response.R;
import com.example.common.util.JwtUtil;
import com.example.user.dto.request.LoginRequest;
import com.example.user.dto.request.RegisterRequest;
import com.example.user.dto.request.SendCodeRequest;
import com.example.user.dto.response.LoginResponse;
import com.example.user.entity.SysUser;
import com.example.user.service.EmailVerificationService;
import com.example.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @Autowired
    EmailVerificationService emailVerificationService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    EmailUtil emailUtil;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public R<Object> register(@RequestBody @Valid RegisterRequest request) {
        emailVerificationService.validateAndConsumeRegisterCode(request.getEmail(), request.getVerifyCode());
        boolean result = authService.register(request);
        return R.ok("注册成功", result);
    }

    @Operation(summary = "发送邮箱验证码")
    @PostMapping("/send-code")
    public R<Object> sendEmailCode(@RequestBody @Valid SendCodeRequest request) {
        emailVerificationService.sendRegisterCode(request.getEmail());
        return R.ok();
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        SysUser sysUser = authService.authenticate(request.getEmail(), request.getPassword());

        String token = jwtUtil.generateToken(sysUser.getId(), sysUser.getName());
        System.out.println(token);
        
        LoginResponse response = LoginResponse.builder()
                .accessToken(token)
                .userId(sysUser.getId().toString())
                .email(sysUser.getEmail()).build();

        return R.ok(response);
    }
}
