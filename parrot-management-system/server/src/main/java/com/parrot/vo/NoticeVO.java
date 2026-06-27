package com.parrot.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告知识展示对象。
 */
@Data
public class NoticeVO {

    private Long id;
    private String title;
    private String content;
    private String type;
    private String coverImage;
    private String publishStatus;
    private LocalDateTime publishTime;
    private Long creatorId;
    private String creatorName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
