package com.example.user.controller;

import com.example.common.response.R;
import com.example.user.dto.request.UserPasswordRequest;
import com.example.user.dto.request.UserProfileRequest;
import com.example.user.dto.response.UserProfileResponse;
import com.example.user.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户个人信息控制器
 * 处理查看和编辑个人信息、修改密码等操作
 */
@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * 查看当前登录用户的个人信息
     *
     * @return 用户个人信息
     */
    @Operation(summary = "查看个人信息")
    @GetMapping("/profile")
    public R<UserProfileResponse> profile() {
        return R.ok("获取个人信息成功", userProfileService.getProfile());
    }

    /**
     * 更新当前登录用户的个人信息
     *
     * @param request 个人信息更新请求（包含邮箱、用户名、手机号、头像）
     * @return 操作结果
     */
    @Operation(summary = "更新个人信息")
    @PutMapping("/updateProfile")
    public R<Object> updateProfile(@Valid @RequestBody UserProfileRequest request) {
        userProfileService.updateProfile(request);
        return R.ok("更新个人信息成功", null);
    }

    /**
     * 修改当前登录用户的密码
     *
     * @param request 密码修改请求（包含旧密码和新密码）
     * @return 操作结果
     */
    @Operation(summary = "更新密码")
    @PutMapping("/updatePassword")
    public R<Object> updatePassword(@Valid @RequestBody UserPasswordRequest request) {
        // TODO 前端传入旧密码需要用密钥对进行加密
        userProfileService.updatePassword(request);
        return R.ok("更新密码成功", null);
    }

}
