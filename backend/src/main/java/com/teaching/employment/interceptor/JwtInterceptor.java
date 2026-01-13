package com.teaching.employment.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teaching.employment.common.Result;
import com.teaching.employment.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * JWT拦截器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    /**
     * 不需要拦截的路径
     */
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/auth/login",
            "/auth/register",
            "/swagger-resources",
            "/v2/api-docs",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html",
            "/doc.html",
            "/webjars",
            "/favicon.ico",
            "/error"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // 处理跨域预检请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String requestURI = request.getRequestURI();

        // 检查是否在排除列表中
        for (String excludePath : EXCLUDE_PATHS) {
            if (requestURI.contains(excludePath)) {
                return true;
            }
        }

        // 获取token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            sendErrorResponse(response, 401, "未登录或token已过期");
            return false;
        }

        // 移除Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证token
        try {
            if (!jwtUtil.validateToken(token)) {
                sendErrorResponse(response, 401, "token无效或已过期");
                return false;
            }

            // 将用户信息存入request attribute
            Long userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);
            Long roleId = jwtUtil.getRoleIdFromToken(token);

            request.setAttribute("userId", userId);
            request.setAttribute("username", username);
            request.setAttribute("roleId", roleId);

            return true;
        } catch (Exception e) {
            log.error("JWT验证失败", e);
            sendErrorResponse(response, 401, "token验证失败");
            return false;
        }
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        Result<?> result = Result.error(message);
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(result));
    }
}
