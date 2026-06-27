package com.parrot.controller;

import com.parrot.common.ApiResult;
import com.parrot.dto.ParrotQueryDTO;
import com.parrot.service.ParrotService;
import com.parrot.vo.ParrotVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台公开鹦鹉展示接口，不需要登录。
 */
@RestController
@RequestMapping("/api/public/parrot")
public class PublicParrotController {

    private final ParrotService parrotService;

    public PublicParrotController(ParrotService parrotService) {
        this.parrotService = parrotService;
    }

    @GetMapping("/list")
    public ApiResult<List<ParrotVO>> list(ParrotQueryDTO query) {
        return ApiResult.success(parrotService.publicList(query));
    }

    @GetMapping("/{id}")
    public ApiResult<ParrotVO> detail(@PathVariable Long id) {
        return ApiResult.success(parrotService.publicDetail(id));
    }
}
