package com.teaching.employment.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密工具类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
public class PasswordUtil {

    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    /**
     * 加密密码
     *
     * @param password 明文密码
     * @return 加密后的密码
     */
    public static String encode(String password) {
        return PASSWORD_ENCODER.encode(password);
    }

    /**
     * 验证密码
     *
     * @param password       明文密码
     * @param encodedPassword 加密后的密码
     * @return true:密码正确 false:密码错误
     */
    public static boolean matches(String password, String encodedPassword) {
        return PASSWORD_ENCODER.matches(password, encodedPassword);
    }
}
