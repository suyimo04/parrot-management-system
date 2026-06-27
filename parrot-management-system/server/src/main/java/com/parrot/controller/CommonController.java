package com.parrot.controller;

import com.parrot.common.ApiResult;
import com.parrot.service.UploadService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 通用接口，目前主要放图片上传。
 */
@RestController
@RequestMapping("/api/common")
public class CommonController {

    private final UploadService uploadService;

    public CommonController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/upload")
    public ApiResult<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) {
        return ApiResult.success("上传成功", uploadService.upload(file));
    }
}
