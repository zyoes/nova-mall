package com.example.common.interceptor;

import com.example.common.core.UserContext;
import com.example.common.exception.CustomUnauthorizedException;
import com.example.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new CustomUnauthorizedException("未提供有效的认证令牌");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new CustomUnauthorizedException("认证令牌无效或已过期");
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        UserContext.set(userId);

        return true;
    }

    /**
     * 请求结束后清理 ThreadLocal，防止线程复用时用户上下文泄漏。
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        UserContext.clear();
    }
}
