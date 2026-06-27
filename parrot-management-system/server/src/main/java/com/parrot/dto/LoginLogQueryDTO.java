package com.parrot.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 登录日志查询条件，时间按日期范围筛选即可。
 */
@Data
public class LoginLogQueryDTO {

    private Long page = 1L;

    private Long size = 10L;

    private String username;

    private String result;

    private LocalDate startDate;

    private LocalDate endDate;
}
