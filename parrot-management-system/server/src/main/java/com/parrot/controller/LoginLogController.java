package com.parrot.controller;

import com.parrot.common.ApiResult;
import com.parrot.common.PageResult;
import com.parrot.dto.LoginLogQueryDTO;
import com.parrot.entity.LoginLog;
import com.parrot.service.LoginLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录日志接口，只给管理员查看。
 */
@RestController
@RequestMapping("/api/admin/login-log")
public class LoginLogController {

    private final LoginLogService loginLogService;

    public LoginLogController(LoginLogService loginLogService) {
        this.loginLogService = loginLogService;
    }

    @GetMapping("/page")
    public ApiResult<PageResult<LoginLog>> page(LoginLogQueryDTO query) {
        return ApiResult.success(loginLogService.page(query));
    }
}
