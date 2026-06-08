package com.example.user.service;

public interface EmailVerificationService {
    String generateAndSaveRegisterCode(String email);

    void validateAndConsumeRegisterCode(String email, String code);

    /**
     * 发送注册验证码（校验邮箱 + 生成验证码 + 发送邮件）
     *
     * @param email 目标邮箱
     */
    void sendRegisterCode(String email);
}
