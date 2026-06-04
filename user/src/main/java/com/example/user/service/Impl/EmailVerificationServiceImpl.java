package com.example.user.service.Impl;

import cn.hutool.core.util.RandomUtil;
import com.example.common.exception.CustomValidationException;
import com.example.user.service.EmailVerificationService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

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

    @Override
    public void validateAndConsumeRegisterCode(String email, String code) {
        // 必须是邮箱收到正确的验证码
        String key = "mall:register:email:code:" + email;
        String codeFromRedis = stringRedisTemplate.opsForValue().get(key);
        if(codeFromRedis == null || !codeFromRedis.equalsIgnoreCase(code)){
            throw new CustomValidationException("验证码错误", 400);
//            return new R<>(400, "验证码错误", null);
        }
        // 验证码校验通过之后从 redis 删除，防止重复使用
        stringRedisTemplate.delete(key);
    }
}
