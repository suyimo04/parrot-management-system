package com.parrot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.parrot.entity.Appointment;
import com.parrot.entity.FeedingRecord;
import com.parrot.entity.Parrot;
import com.parrot.entity.SysUser;
import com.parrot.mapper.AppointmentMapper;
import com.parrot.mapper.DashboardMapper;
import com.parrot.mapper.FeedingRecordMapper;
import com.parrot.mapper.ParrotMapper;
import com.parrot.mapper.SysUserMapper;
import com.parrot.service.DashboardService;
import com.parrot.vo.ChartItemVO;
import com.parrot.vo.DashboardChartsVO;
import com.parrot.vo.DashboardStatsVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 首页看板统计，数据量不大时直接用 MyBatis-Plus 和 Java 汇总即可。
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    private final ParrotMapper parrotMapper;
    private final SysUserMapper sysUserMapper;
    private final AppointmentMapper appointmentMapper;
    private final FeedingRecordMapper feedingRecordMapper;
    private final DashboardMapper dashboardMapper;

    public DashboardServiceImpl(ParrotMapper parrotMapper,
                                SysUserMapper sysUserMapper,
                                AppointmentMapper appointmentMapper,
                                FeedingRecordMapper feedingRecordMapper,
                                DashboardMapper dashboardMapper) {
        this.parrotMapper = parrotMapper;
        this.sysUserMapper = sysUserMapper;
        this.appointmentMapper = appointmentMapper;
        this.feedingRecordMapper = feedingRecordMapper;
        this.dashboardMapper = dashboardMapper;
    }

    @Override
    public DashboardStatsVO stats() {
        DashboardStatsVO vo = new DashboardStatsVO();
        vo.setParrotCount(parrotMapper.selectCount(null));
        vo.setCustomerCount(sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getRole, "CUSTOMER")));
        vo.setPendingAppointmentCount(appointmentMapper.selectCount(new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getStatus, "待处理")));
        vo.setAbnormalHealthCount(parrotMapper.selectCount(new LambdaQueryWrapper<Parrot>()
                .in(Parrot::getHealthStatus, "观察中", "治疗中")));
        vo.setTodayFeedingCount(feedingRecordMapper.selectCount(new LambdaQueryWrapper<FeedingRecord>()
                .eq(FeedingRecord::getFeedingDate, LocalDate.now())));
        return vo;
    }

    @Override
    public DashboardChartsVO charts() {
        DashboardChartsVO vo = new DashboardChartsVO();
        vo.setSpeciesDistribution(dashboardMapper.selectSpeciesDistribution());
        vo.setHealthStatusDistribution(dashboardMapper.selectHealthStatusDistribution());
        vo.setAppointmentTrend(appointmentTrend());
        return vo;
    }

    private List<ChartItemVO> appointmentTrend() {
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(6);
        Map<String, Long> countMap = dashboardMapper.selectAppointmentTrend(start, today)
                .stream()
                .collect(Collectors.toMap(ChartItemVO::getName, ChartItemVO::getValue));
        List<ChartItemVO> list = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = start.plusDays(i);
            String name = date.toString();
            list.add(new ChartItemVO(name, countMap.getOrDefault(name, 0L)));
        }
        return list;
    }
}
