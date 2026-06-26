package com.parrot.common;

/**
 * 项目里约定的几个基础状态码，不额外搞复杂权限码。
 */
public class ResultCode {

    public static final int SUCCESS = 200;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int ERROR = 500;

    private ResultCode() {
    }
}
