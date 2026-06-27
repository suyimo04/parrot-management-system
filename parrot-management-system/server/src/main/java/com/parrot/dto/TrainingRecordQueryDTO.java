package com.parrot.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 训练记录查询条件。
 */
@Data
public class TrainingRecordQueryDTO {

    private Long page = 1L;

    private Long size = 10L;

    private Long parrotId;

    private String project;

    private LocalDate startDate;

    private LocalDate endDate;
}
