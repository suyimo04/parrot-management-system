package com.parrot.service.impl;

import com.parrot.common.BusinessException;
import com.parrot.common.CurrentUserContext;
import com.parrot.common.FileNameUtils;
import com.parrot.entity.FileResource;
import com.parrot.mapper.FileResourceMapper;
import com.parrot.service.UploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

/**
 * 图片上传到本地目录，返回的地址可以直接存到业务表里。
 */
@Service
public class UploadServiceImpl implements UploadService {

    private static final long MAX_SIZE = 10 * 1024 * 1024;
    private static final Set<String> ALLOW_EXT = Set.of("jpg", "jpeg", "png", "webp", "gif");
    private static final DateTimeFormatter DATE_DIR = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final FileResourceMapper fileResourceMapper;

    @Value("${parrot.upload.save-path}")
    private String savePath;

    @Value("${parrot.upload.access-prefix}")
    private String accessPrefix;

    public UploadServiceImpl(FileResourceMapper fileResourceMapper) {
        this.fileResourceMapper = fileResourceMapper;
    }

    @Override
    public Map<String, Object> upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的图片");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException("图片大小不能超过10MB");
        }
        String originalName = file.getOriginalFilename();
        String ext = StringUtils.getFilenameExtension(originalName);
        if (ext == null || !ALLOW_EXT.contains(ext.toLowerCase())) {
            throw new BusinessException("只支持 jpg、jpeg、png、webp、gif 图片");
        }

        String dirName = LocalDate.now().format(DATE_DIR);
        String storeName = FileNameUtils.buildStoreName(originalName);
        Path dir = Path.of(savePath, dirName);
        Path target = dir.resolve(storeName);
        try {
            Files.createDirectories(dir);
            file.transferTo(target.toFile());
        } catch (IOException e) {
            throw new BusinessException("图片保存失败，请检查上传目录");
        }

        String prefix = accessPrefix.endsWith("/") ? accessPrefix.substring(0, accessPrefix.length() - 1) : accessPrefix;
        String accessUrl = prefix + "/" + dirName + "/" + storeName;
        FileResource resource = new FileResource();
        resource.setOriginalName(originalName);
        resource.setAccessUrl(accessUrl);
        resource.setFileType(ext.toLowerCase());
        resource.setFileSize(file.getSize());
        resource.setUploaderId(CurrentUserContext.getUserId());
        resource.setUploadTime(LocalDateTime.now());
        fileResourceMapper.insert(resource);

        return Map.of(
                "id", resource.getId(),
                "originalName", originalName,
                "accessUrl", accessUrl,
                "url", accessUrl,
                "fileType", ext.toLowerCase(),
                "fileSize", file.getSize()
        );
    }
}
