package com.parrot.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 训练记录，用来记录训练项目、配合程度和下次建议。
 */
@Data
@TableName("training_record")
public class TrainingRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parrotId;

    private LocalDate trainingDate;

    private Integer duration;

    private String project;

    private String content;

    private String cooperation;

    private String completion;

    private String reward;

    private String nextSuggestion;

    private Long recorderId;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
