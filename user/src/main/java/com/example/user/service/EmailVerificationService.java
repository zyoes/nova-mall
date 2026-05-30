package com.example.user.service;

public interface EmailVerificationService {
    String generateAndSaveRegisterCode(String email);

    void validateAndConsumeRegisterCode(String email, String code);
}
