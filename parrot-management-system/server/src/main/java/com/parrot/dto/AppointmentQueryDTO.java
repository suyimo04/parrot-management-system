package com.parrot.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 后台预约分页查询条件。
 */
@Data
public class AppointmentQueryDTO {

    private Long page = 1L;

    private Long size = 10L;

    private String status;

    private String customerKeyword;

    private Long parrotId;

    private LocalDate startDate;

    private LocalDate endDate;
}
