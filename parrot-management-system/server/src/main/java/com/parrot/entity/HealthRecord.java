package com.parrot.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 健康记录，填写异常症状后会影响鹦鹉健康状态。
 */
@Data
@TableName("health_record")
public class HealthRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parrotId;

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

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
