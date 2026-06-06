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

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;

    @Operation(summary = "查看个人信息")
    @GetMapping("/profile")
    public R<UserProfileResponse> profile() {
        return R.ok("获取个人信息成功", userProfileService.getProfile());
    }

    @Operation(summary = "更新个人信息")
    @PutMapping("/updateProfile")
    public R<Object> updateProfile(@Valid @RequestBody UserProfileRequest request) {
        userProfileService.updateProfile(request);
        return R.ok("更新个人信息成功", null);
    }

    @Operation(summary = "更新密码")
    @PutMapping("/updatePassword")
    public R<Object> updatePassword(@Valid @RequestBody UserPasswordRequest request) {
        // TODO 前端传入旧密码需要用密钥对进行加密
        userProfileService.updatePassword(request);
        return R.ok("更新密码成功", null);
    }

}
