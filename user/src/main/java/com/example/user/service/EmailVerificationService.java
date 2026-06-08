package com.example.user.service;

/**
 * 邮箱验证码服务接口
 * 提供验证码生成、校验及发送等功能
 */
public interface EmailVerificationService {

    /**
     * 生成注册验证码并保存到 Redis（有效期 5 分钟）
     *
     * @param email 目标邮箱
     * @return 生成的验证码
     */
    String generateAndSaveRegisterCode(String email);

    /**
     * 校验注册验证码是否正确，校验通过后删除验证码（防止重复使用）
     *
     * @param email 目标邮箱
     * @param code  用户输入的验证码
     * @throws CustomValidationException 验证码错误或已过期时抛出
     */
    void validateAndConsumeRegisterCode(String email, String code);

    /**
     * 发送注册验证码（校验邮箱 + 生成验证码 + 发送邮件）
     *
     * @param email 目标邮箱
     */
    void sendRegisterCode(String email);
}
