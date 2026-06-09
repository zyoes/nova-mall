package com.example.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.user.dto.request.RegisterRequest;
import com.example.user.entity.SysUser;

/**
 * 认证服务接口
 * 提供用户登录、注册及邮箱/手机号唯一性校验等功能
 */
public interface AuthService extends IService<SysUser> {

    /**
     * 用户登录认证
     *
     * @param email    用户邮箱
     * @param password 用户密码（明文）
     * @return 认证通过的用户实体
     * @throws CustomUnauthorizedException 邮箱或密码错误时抛出
     */
    SysUser authenticate(String email, String password);

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 是否注册成功
     */
    boolean register(RegisterRequest request);

    /**
     * 校验邮箱是否已被注册
     *
     * @param email 待校验邮箱
     * @throws CustomValidationException 邮箱已被注册时抛出
     */
    void ensureEmailNotRegistered(String email);

    /**
     * 校验手机号是否已被注册
     *
     * @param mobile 待校验手机号
     * @throws CustomValidationException 手机号已被使用时抛出
     */
    void ensureMobileNotRegistered(String mobile);
}
