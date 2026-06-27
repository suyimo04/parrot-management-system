package com.parrot.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 喂养记录保存参数。
 */
@Data
public class FeedingRecordSaveDTO {

    @NotNull(message = "鹦鹉不能为空")
    private Long parrotId;

    @NotNull(message = "喂养日期不能为空")
    private LocalDate feedingDate;

    private LocalTime feedingTime;

    private String mainFood;

    private String supplement;

    private String amount;

    private String water;

    private Integer isFinished;

    private String leftover;

    private String remark;
}
