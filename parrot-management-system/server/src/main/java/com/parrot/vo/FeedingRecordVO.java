package com.parrot.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 喂养记录展示对象。
 */
@Data
public class FeedingRecordVO {

    private Long id;
    private Long parrotId;
    private String parrotName;
    private LocalDate feedingDate;
    private LocalTime feedingTime;
    private String mainFood;
    private String supplement;
    private String amount;
    private String water;
    private Integer isFinished;
    private String leftover;
    private Long recorderId;
    private String recorderName;
    private String remark;
    private LocalDateTime createTime;
}
