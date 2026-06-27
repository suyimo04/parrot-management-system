package com.parrot.dto;

import lombok.Data;

/**
 * 公告知识分页查询条件。
 */
@Data
public class NoticeQueryDTO {

    private Long page = 1L;

    private Long size = 10L;

    private String title;

    private String type;

    private String publishStatus;
}
