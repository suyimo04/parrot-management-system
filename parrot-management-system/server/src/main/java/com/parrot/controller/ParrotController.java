package com.parrot.controller;

import com.parrot.common.ApiResult;
import com.parrot.common.PageResult;
import com.parrot.dto.ParrotQueryDTO;
import com.parrot.dto.ParrotSaveDTO;
import com.parrot.service.ParrotService;
import com.parrot.vo.ParrotVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台鹦鹉档案接口。
 */
@RestController
@RequestMapping("/api/admin/parrot")
public class ParrotController {

    private final ParrotService parrotService;

    public ParrotController(ParrotService parrotService) {
        this.parrotService = parrotService;
    }

    @GetMapping("/page")
    public ApiResult<PageResult<ParrotVO>> page(ParrotQueryDTO query) {
        return ApiResult.success(parrotService.page(query));
    }

    @PostMapping
    public ApiResult<ParrotVO> add(@Valid @RequestBody ParrotSaveDTO dto) {
        return ApiResult.success("新增鹦鹉档案成功", parrotService.add(dto));
    }

    @PutMapping("/{id}")
    public ApiResult<ParrotVO> update(@PathVariable Long id, @Valid @RequestBody ParrotSaveDTO dto) {
        return ApiResult.success("修改鹦鹉档案成功", parrotService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        parrotService.delete(id);
        return ApiResult.success("鹦鹉档案已删除或停用", null);
    }
}
