package com.teaching.employment.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Component
public class JwtUtil {

    /**
     * JWT密钥
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * JWT过期时间（毫秒）
     */
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 生成JWT token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param roleId   角色ID
     * @return JWT token
     */
    public String generateToken(Long userId, String username, Long roleId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("roleId", roleId);
        return generateToken(claims);
    }

    /**
     * 生成JWT token
     *
     * @param claims 自定义声明
     * @return JWT token
     */
    public String generateToken(Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 从token中获取Claims
     *
     * @param token JWT token
     * @return Claims
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从token中获取用户ID
     *
     * @param token JWT token
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从token中获取用户名
     *
     * @param token JWT token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("username", String.class);
    }

    /**
     * 从token中获取角色ID
     *
     * @param token JWT token
     * @return 角色ID
     */
    public Long getRoleIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("roleId", Long.class);
    }

    /**
     * 验证token是否有效
     *
     * @param token JWT token
     * @return true:有效 false:无效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return !isTokenExpired(claims);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断token是否过期
     *
     * @param claims Claims
     * @return true:过期 false:未过期
     */
    private boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }

    /**
     * 刷新token
     *
     * @param token 旧的JWT token
     * @return 新的JWT token
     */
    public String refreshToken(String token) {
        Claims claims = getClaimsFromToken(token);
        claims.setIssuedAt(new Date());
        return generateToken(claims);
    }

    /**
     * 获取签名密钥
     *
     * @return 密钥
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
