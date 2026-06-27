package com.parrot.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 鹦鹉档案保存参数，适配后台档案表单。
 */
@Data
public class ParrotSaveDTO {

    @NotBlank(message = "档案编号不能为空")
    private String code;

    @NotBlank(message = "鹦鹉名称不能为空")
    private String name;

    @NotNull(message = "品种不能为空")
    private Long speciesId;

    private String gender;

    @Min(value = 0, message = "年龄不能小于0")
    private Integer age;

    private LocalDate birthDate;

    private String color;

    private String image;

    @NotBlank(message = "健康状态不能为空")
    private String healthStatus;

    private String source;

    private LocalDate entryDate;

    private String personality;

    private String careNotes;

    @NotBlank(message = "当前状态不能为空")
    private String currentStatus;

    private String description;

    @NotNull(message = "公开状态不能为空")
    private Integer isPublic;
}
