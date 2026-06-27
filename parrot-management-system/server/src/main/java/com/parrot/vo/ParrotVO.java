package com.parrot.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 鹦鹉档案展示对象，附带品种名称方便列表直接显示。
 */
@Data
public class ParrotVO {

    private Long id;
    private String code;
    private String name;
    private Long speciesId;
    private String speciesName;
    private String speciesEnglishName;
    private String speciesOrigin;
    private String speciesSize;
    private String speciesAvgLifespan;
    private String speciesHabits;
    private String speciesDifficulty;
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
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
