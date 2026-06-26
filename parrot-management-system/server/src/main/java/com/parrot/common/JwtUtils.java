package com.parrot.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * JWT 生成和解析工具，Token 里只放基础身份信息。
 */
@Component
public class JwtUtils {

    @Value("${parrot.jwt.secret}")
    private String secret;

    @Value("${parrot.jwt.expire-hours}")
    private Long expireHours;

    public String createToken(CurrentUser user) {
        Date now = new Date();
        Date expireAt = Date.from(LocalDateTime.now()
                .plusHours(expireHours)
                .atZone(ZoneId.systemDefault())
                .toInstant());
        return Jwts.builder()
                .subject(String.valueOf(user.getUserId()))
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
                .issuedAt(now)
                .expiration(expireAt)
                .signWith(getKey())
                .compact();
    }

    public CurrentUser parseToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            Long userId = Long.valueOf(claims.getSubject());
            String username = claims.get("username", String.class);
            String role = claims.get("role", String.class);
            return new CurrentUser(userId, username, role);
        } catch (JwtException | IllegalArgumentException e) {
            throw new AuthException(ResultCode.UNAUTHORIZED, "登录状态已失效，请重新登录");
        }
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
