package com.parrot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 品种保存参数，字段基本对应品种维护表单。
 */
@Data
public class SpeciesSaveDTO {

    @NotBlank(message = "品种名称不能为空")
    private String name;

    private String englishName;

    private String origin;

    private String size;

    private String avgLifespan;

    private String habits;

    private String difficulty;

    private String image;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
