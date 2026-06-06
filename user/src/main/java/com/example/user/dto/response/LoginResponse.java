package com.example.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String accessToken;

    private String tokenType = "Bearer";

    private Long expiresIn;

    private String userId;

    private String email;
}
