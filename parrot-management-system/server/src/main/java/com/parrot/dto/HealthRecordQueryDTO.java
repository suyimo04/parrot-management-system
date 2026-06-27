package com.parrot.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 健康记录查询条件。
 */
@Data
public class HealthRecordQueryDTO {

    private Long page = 1L;

    private Long size = 10L;

    private Long parrotId;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer hasAbnormal;
}
