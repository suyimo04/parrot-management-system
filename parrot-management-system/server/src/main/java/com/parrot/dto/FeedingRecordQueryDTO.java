package com.parrot.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 喂养记录查询条件。
 */
@Data
public class FeedingRecordQueryDTO {

    private Long page = 1L;

    private Long size = 10L;

    private Long parrotId;

    private Long recorderId;

    private LocalDate startDate;

    private LocalDate endDate;
}
