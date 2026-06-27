package com.parrot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 上传后的文件记录，方便后续追踪图片来源。
 */
@Data
@TableName("file_resource")
public class FileResource {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String originalName;

    private String accessUrl;

    private String fileType;

    private Long fileSize;

    private Long uploaderId;

    private LocalDateTime uploadTime;
}
