package com.parrot.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 本地图片上传业务。
 */
public interface UploadService {

    Map<String, Object> upload(MultipartFile file);
}
