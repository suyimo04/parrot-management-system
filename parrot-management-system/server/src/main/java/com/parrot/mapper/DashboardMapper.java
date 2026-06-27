package com.parrot.mapper;

import com.parrot.vo.ChartItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 看板统计 Mapper，涉及分组统计的 SQL 放到 XML 里更直观。
 */
@Mapper
public interface DashboardMapper {

    List<ChartItemVO> selectSpeciesDistribution();

    List<ChartItemVO> selectHealthStatusDistribution();

    List<ChartItemVO> selectAppointmentTrend(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);
}
