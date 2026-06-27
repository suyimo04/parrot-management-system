package com.parrot.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 喂养记录，主要用于饲养员日常登记。
 */
@Data
@TableName("feeding_record")
public class FeedingRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parrotId;

    private LocalDate feedingDate;

    private LocalTime feedingTime;

    private String mainFood;

    private String supplement;

    private String amount;

    private String water;

    private Integer isFinished;

    private String leftover;

    private Long recorderId;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
