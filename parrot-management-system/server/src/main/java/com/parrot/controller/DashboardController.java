package com.parrot.controller;

import com.parrot.common.ApiResult;
import com.parrot.service.DashboardService;
import com.parrot.vo.DashboardChartsVO;
import com.parrot.vo.DashboardStatsVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台首页看板接口。
 */
@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ApiResult<DashboardStatsVO> stats() {
        return ApiResult.success(dashboardService.stats());
    }

    @GetMapping("/charts")
    public ApiResult<DashboardChartsVO> charts() {
        return ApiResult.success(dashboardService.charts());
    }
}
