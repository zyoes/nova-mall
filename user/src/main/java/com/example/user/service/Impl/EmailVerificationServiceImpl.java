package com.example.user.service.Impl;

import cn.hutool.core.util.RandomUtil;
import com.example.common.exception.CustomValidationException;
import com.example.user.util.EmailUtil;
import com.example.user.service.AuthService;
import com.example.user.service.EmailVerificationService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 邮箱验证码服务实现类
 */
@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private final AuthService authService;

    private final EmailUtil emailUtil;

    /**
     * 生成 6 位随机验证码并保存到 Redis，有效期 5 分钟
     *
     * @param email 目标邮箱
     * @return 生成的验证码
     */
    @Override
    public String generateAndSaveRegisterCode(String email) {
        // 生成验证码
        String code = RandomUtil.randomString(6);

        // 保存验证码（要以当前邮箱关联，一定时间内有效）
        // 谁谁谁.帮我操作
        String key = "mall:register:email:code:" + email;
        stringRedisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);

        return code;
    }

    /**
     * 校验验证码是否正确，校验通过后从 Redis 删除（防止重复使用）
     *
     * @param email 目标邮箱
     * @param code  用户输入的验证码
     * @throws CustomValidationException 验证码错误或已过期时抛出
     */
    @Override
    public void validateAndConsumeRegisterCode(String email, String code) {
        // 必须是邮箱收到正确的验证码
        String key = "mall:register:email:code:" + email;
        String codeFromRedis = stringRedisTemplate.opsForValue().get(key);
        if(codeFromRedis == null || !codeFromRedis.equalsIgnoreCase(code)){
            throw new CustomValidationException("验证码错误", 400);
        }

        // 验证码校验通过之后从 redis 删除，防止重复使用
        stringRedisTemplate.delete(key);
    }

    /**
     * 发送注册验证码
     * 编排流程：校验邮箱唯一性 → 生成验证码 → 发送邮件
     *
     * @param email 目标邮箱
     */
    @Override
    public void sendRegisterCode(String email) {
        // 1. 校验邮箱是否已注册
        authService.ensureEmailNotRegistered(email);

        // 2. 生成并保存验证码
        String code = generateAndSaveRegisterCode(email);

        // 3. 发送验证码邮件
        emailUtil.sendEmailCode(email, code);
    }
}
