package com.parrot.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 健康记录展示对象，补充鹦鹉和记录人名称。
 */
@Data
public class HealthRecordVO {

    private Long id;
    private Long parrotId;
    private String parrotName;
    private LocalDate recordDate;
    private BigDecimal weight;
    private String spirit;
    private String appetite;
    private String feather;
    private String excrement;
    private String abnormalSymptoms;
    private String treatment;
    private Integer needReview;
    private LocalDate reviewDate;
    private Long recorderId;
    private String recorderName;
    private String remark;
    private LocalDateTime createTime;
}
