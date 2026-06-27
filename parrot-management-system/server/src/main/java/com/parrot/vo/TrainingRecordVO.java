package com.parrot.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 训练记录展示对象。
 */
@Data
public class TrainingRecordVO {

    private Long id;
    private Long parrotId;
    private String parrotName;
    private LocalDate trainingDate;
    private Integer duration;
    private String project;
    private String content;
    private String cooperation;
    private String completion;
    private String reward;
    private String nextSuggestion;
    private Long recorderId;
    private String recorderName;
    private String remark;
    private LocalDateTime createTime;
}
