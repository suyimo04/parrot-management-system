package com.parrot.service;

import com.parrot.vo.DashboardChartsVO;
import com.parrot.vo.DashboardStatsVO;

/**
 * 后台首页看板业务。
 */
public interface DashboardService {

    DashboardStatsVO stats();

    DashboardChartsVO charts();
}
