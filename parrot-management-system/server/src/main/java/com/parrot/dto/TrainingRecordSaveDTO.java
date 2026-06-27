package com.parrot.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 训练记录保存参数。
 */
@Data
public class TrainingRecordSaveDTO {

    @NotNull(message = "鹦鹉不能为空")
    private Long parrotId;

    @NotNull(message = "训练日期不能为空")
    private LocalDate trainingDate;

    @Min(value = 0, message = "训练时长不能小于0")
    private Integer duration;

    @NotBlank(message = "训练项目不能为空")
    private String project;

    private String content;

    private String cooperation;

    private String completion;

    private String reward;

    private String nextSuggestion;

    private String remark;
}
