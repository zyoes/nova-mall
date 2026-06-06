package com.example.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.user.dto.request.UserPasswordRequest;
import com.example.user.dto.request.UserProfileRequest;
import com.example.user.dto.response.UserProfileResponse;
import com.example.user.entity.SysUser;

public interface UserProfileService extends IService<SysUser> {

    /**
     * 查看当前用户个人信息
     */
    UserProfileResponse getProfile();

    /**
     * 编辑个人信息
     */
    void updateProfile(UserProfileRequest request);

    /**
     * 修改密码
     */
    void updatePassword(UserPasswordRequest request);

}