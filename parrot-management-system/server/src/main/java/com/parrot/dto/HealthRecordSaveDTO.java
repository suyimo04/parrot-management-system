package com.parrot.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 健康记录保存参数。
 */
@Data
public class HealthRecordSaveDTO {

    @NotNull(message = "鹦鹉不能为空")
    private Long parrotId;

    @NotNull(message = "记录日期不能为空")
    private LocalDate recordDate;

    @DecimalMin(value = "0.0", message = "体重不能小于0")
    private BigDecimal weight;

    private String spirit;

    private String appetite;

    private String feather;

    private String excrement;

    private String abnormalSymptoms;

    private String treatment;

    private Integer needReview;

    private LocalDate reviewDate;

    private String remark;
}
