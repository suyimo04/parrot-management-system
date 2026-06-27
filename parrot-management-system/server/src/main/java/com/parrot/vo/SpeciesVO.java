package com.parrot.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 品种展示对象。
 */
@Data
public class SpeciesVO {

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
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
