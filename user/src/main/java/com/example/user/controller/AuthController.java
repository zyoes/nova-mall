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
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理用户注册、登录、验证码等认证相关操作
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final EmailVerificationService emailVerificationService;

    private final JwtUtil jwtUtil;

    /**
     * 用户注册
     *
     * @param request 注册请求（包含邮箱、密码、用户名、手机号、验证码）
     * @return 注册结果
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public R<Object> register(@RequestBody @Valid RegisterRequest request) {
        emailVerificationService.validateAndConsumeRegisterCode(request.getEmail(), request.getVerifyCode());
        boolean result = authService.register(request);
        return R.ok("注册成功", result);
    }

    /**
     * 发送邮箱验证码
     *
     * @param request 发送验证码请求（包含目标邮箱）
     * @return 操作结果
     */
    @Operation(summary = "发送邮箱验证码")
    @PostMapping("/send-code")
    public R<Object> sendEmailCode(@RequestBody @Valid SendCodeRequest request) {
        emailVerificationService.sendRegisterCode(request.getEmail());
        return R.ok();
    }

    /**
     * 用户登录
     *
     * @param request 登录请求（包含邮箱和密码）
     * @return 登录响应（包含 accessToken、userId、email）
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        SysUser sysUser = authService.authenticate(request.getEmail(), request.getPassword());

        String token = jwtUtil.generateToken(sysUser.getId(), sysUser.getName());
        
        LoginResponse response = LoginResponse.builder()
                .accessToken(token)
                .userId(sysUser.getId().toString())
                .email(sysUser.getEmail()).build();

        return R.ok(response);
    }
}
