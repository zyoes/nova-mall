package com.example.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.user.dao.request.RegisterRequest;
import com.example.user.entity.SysUser;

public interface AuthService extends IService<SysUser> {
    SysUser authenticate(String email, String password);

    boolean register(RegisterRequest request);

    void ensureEmailNotRegistered(String email);

    void ensureMobileNotRegistered(String mobile);
}
