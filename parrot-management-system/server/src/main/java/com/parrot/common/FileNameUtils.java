package com.parrot.common;

import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 处理上传文件名，避免中文名或重名影响保存。
 */
public class FileNameUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private FileNameUtils() {
    }

    public static String buildStoreName(String originalName) {
        String ext = StringUtils.getFilenameExtension(originalName);
        String name = LocalDateTime.now().format(FORMATTER) + "_" + UUID.randomUUID().toString().replace("-", "");
        return ext == null ? name : name + "." + ext.toLowerCase();
    }
}
