package com.parrot.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 鹦鹉品种资料，后台维护后给档案和前台筛选使用。
 */
@Data
@TableName("parrot_species")
public class ParrotSpecies {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String englishName;

    private String origin;

    private String size;

    private String avgLifespan;

    private String habits;

    private String difficulty;

    private String image;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
