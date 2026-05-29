package com.example.common.util;

import com.example.common.exception.CustomUnauthorizedException;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
public class JwtUtil {
    @Value("${auth.jwt.secret-key}")
    private String secretKey;

    @Value("${auth.jwt.expires-in}")
    private Long expiresIn;

    private JwtEncoder jwtEncoder;

    private JwtDecoder jwtDecoder;

    @PostConstruct
    public void init() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec key = new SecretKeySpec(keyBytes, "HmacSHA256");
        this.jwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
        this.jwtDecoder = NimbusJwtDecoder.withSecretKey(key).build();
    }

    // 签发 token
    public String generateToken(Long userId,String username) {
        // 生成 token 的逻辑
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("nova-mall")
                .subject(username)
                .issuedAt(now)
                .expiresAt(now.plusMillis(expiresIn))
                .claim("userId", userId)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    // 校验 token
    public boolean validateToken(String token) {
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (JwtException e) {
            throw new CustomUnauthorizedException("认证令牌无效或已过期");
        }
    }

    // 从 token 中提取 userId
    public Long getUserIdFromToken(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getClaim("userId");
    }
}
