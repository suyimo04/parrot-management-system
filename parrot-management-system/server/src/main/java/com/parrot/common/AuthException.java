package com.parrot.common;

/**
 * 登录状态或权限校验失败时使用，拦截器里抛出来更直观。
 */
public class AuthException extends BusinessException {

    public AuthException(Integer code, String message) {
        super(code, message);
    }
}
