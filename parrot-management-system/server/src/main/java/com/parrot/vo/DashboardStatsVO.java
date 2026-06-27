package com.parrot.vo;

import lombok.Data;

/**
 * 后台首页统计卡片数据。
 */
@Data
public class DashboardStatsVO {

    private Long parrotCount;
    private Long customerCount;
    private Long pendingAppointmentCount;
    private Long abnormalHealthCount;
    private Long todayFeedingCount;
}
