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
 * 鹦鹉档案，是系统后续养护记录和预约咨询的基础数据。
 */
@Data
@TableName("parrot")
public class Parrot {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String code;

    private String name;

    private Long speciesId;

    private String gender;

    private Integer age;

    private LocalDate birthDate;

    private String color;

    private String image;

    private String healthStatus;

    private String source;

    private LocalDate entryDate;

    private String personality;

    private String careNotes;

    private String currentStatus;

    private String description;

    private Integer isPublic;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
