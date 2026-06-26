package com.parrot.common;

import lombok.Getter;

/**
 * 业务里能预判的问题直接抛这个异常，统一由全局异常处理返回。
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        this(ResultCode.BAD_REQUEST, message);
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
