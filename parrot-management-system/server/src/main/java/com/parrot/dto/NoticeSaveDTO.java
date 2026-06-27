package com.parrot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 公告知识保存参数。
 */
@Data
public class NoticeSaveDTO {

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    @NotBlank(message = "类型不能为空")
    private String type;

    private String coverImage;

    @NotBlank(message = "发布状态不能为空")
    private String publishStatus;
}
