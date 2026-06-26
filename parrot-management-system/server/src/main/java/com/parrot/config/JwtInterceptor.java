package com.parrot.config;

import com.parrot.common.AuthException;
import com.parrot.common.CurrentUser;
import com.parrot.common.CurrentUserContext;
import com.parrot.common.JwtUtils;
import com.parrot.common.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

/**
 * 解析 Token 并做路径权限判断，规则写得直白一点，答辩时也方便说明。
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final Set<String> ADMIN_ONLY = Set.of("/api/admin/user/**", "/api/admin/login-log/**");
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final JwtUtils jwtUtils;

    public JwtInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = readToken(request);
        CurrentUser user = jwtUtils.parseToken(token);
        checkPermission(request.getRequestURI(), user);
        CurrentUserContext.set(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        CurrentUserContext.clear();
    }

    private String readToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (!StringUtils.hasText(header) || !header.startsWith(TOKEN_PREFIX)) {
            throw new AuthException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        String token = header.substring(TOKEN_PREFIX.length());
        if (!StringUtils.hasText(token)) {
            throw new AuthException(ResultCode.UNAUTHORIZED, "Token 不能为空");
        }
        return token;
    }

    private void checkPermission(String uri, CurrentUser user) {
        String role = user.getRole();
        if (match(uri, "/api/common/upload")) {
            return;
        }
        if (match(uri, "/api/admin/**")) {
            if (!"ADMIN".equals(role) && !"KEEPER".equals(role)) {
                throw new AuthException(ResultCode.FORBIDDEN, "无权限访问后台接口");
            }
            if (isAdminOnly(uri) && !"ADMIN".equals(role)) {
                throw new AuthException(ResultCode.FORBIDDEN, "该功能仅管理员可用");
            }
            return;
        }
        if (match(uri, "/api/portal/**")) {
            if (!"CUSTOMER".equals(role)) {
                throw new AuthException(ResultCode.FORBIDDEN, "当前账号不能访问前台个人接口");
            }
            return;
        }
        throw new AuthException(ResultCode.FORBIDDEN, "无权限访问该接口");
    }

    private boolean isAdminOnly(String uri) {
        return ADMIN_ONLY.stream().anyMatch(pattern -> match(uri, pattern));
    }

    private boolean match(String uri, String pattern) {
        return pathMatcher.match(pattern, uri);
    }
}
