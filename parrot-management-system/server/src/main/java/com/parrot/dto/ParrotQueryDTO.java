package com.parrot.dto;

import lombok.Data;

/**
 * 鹦鹉档案查询条件，关键词匹配编号和名称。
 */
@Data
public class ParrotQueryDTO {

    private Long page = 1L;

    private Long size = 10L;

    private String keyword;

    private Long speciesId;

    private String healthStatus;

    private Integer isPublic;
}
