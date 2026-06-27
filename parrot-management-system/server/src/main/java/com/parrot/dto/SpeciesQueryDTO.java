package com.parrot.dto;

import lombok.Data;

/**
 * 品种分页查询条件。
 */
@Data
public class SpeciesQueryDTO {

    private Long page = 1L;

    private Long size = 10L;

    private String name;

    private Integer status;
}
