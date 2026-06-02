package com.example.common.util;

import com.example.common.exception.CustomUnauthorizedException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${auth.jwt.secret-key}")
    private String secretKey;

    @Value("${auth.jwt.expires-in}")
    private Long expiresIn;

    private byte[] keyBytes;
    private MACSigner macSigner;
    private NimbusJwtDecoder jwtDecoder;

    @PostConstruct
    public void init() throws KeyLengthException {
        this.keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.macSigner = new MACSigner(this.keyBytes);
        SecretKeySpec key = new SecretKeySpec(this.keyBytes, "HmacSHA256");
        this.jwtDecoder = NimbusJwtDecoder.withSecretKey(key).build();
    }

    public String generateToken(Long userId, String username) {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer("nova-mall")
                .subject(username)
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + expiresIn))
                .claim("userId", userId)
                .build();

        try {
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            signedJWT.sign(macSigner);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Failed to generate JWT", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (JwtException e) {
            throw new CustomUnauthorizedException("认证令牌无效或已过期");
        }
    }

    public Long getUserIdFromToken(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getClaim("userId");
    }
}