package com.parrot.vo;

import lombok.Data;

import java.util.List;

/**
 * 看板图表数据，前端 ECharts 可以直接按数组渲染。
 */
@Data
public class DashboardChartsVO {

    private List<ChartItemVO> speciesDistribution;

    private List<ChartItemVO> healthStatusDistribution;

    private List<ChartItemVO> appointmentTrend;
}
