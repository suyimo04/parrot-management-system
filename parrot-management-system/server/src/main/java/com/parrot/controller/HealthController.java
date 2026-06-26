package com.parrot.controller;

import com.parrot.common.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简单健康检查，前期主要用来确认后端服务已经起来。
 */
@RestController
public class HealthController {

    @GetMapping("/api/public/health")
    public ApiResult<String> health() {
        return ApiResult.success("鹦鹉管理系统后端运行正常");
    }
}
