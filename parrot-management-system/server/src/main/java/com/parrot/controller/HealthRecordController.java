package com.parrot.controller;

import com.parrot.common.ApiResult;
import com.parrot.common.PageResult;
import com.parrot.dto.HealthRecordQueryDTO;
import com.parrot.dto.HealthRecordSaveDTO;
import com.parrot.service.HealthRecordService;
import com.parrot.vo.HealthRecordVO;
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
 * 后台健康记录接口。
 */
@RestController
@RequestMapping("/api/admin/health-record")
public class HealthRecordController {

    private final HealthRecordService healthRecordService;

    public HealthRecordController(HealthRecordService healthRecordService) {
        this.healthRecordService = healthRecordService;
    }

    @GetMapping("/page")
    public ApiResult<PageResult<HealthRecordVO>> page(HealthRecordQueryDTO query) {
        return ApiResult.success(healthRecordService.page(query));
    }

    @PostMapping
    public ApiResult<HealthRecordVO> add(@Valid @RequestBody HealthRecordSaveDTO dto) {
        return ApiResult.success("健康记录已保存", healthRecordService.add(dto));
    }

    @PutMapping("/{id}")
    public ApiResult<HealthRecordVO> update(@PathVariable Long id, @Valid @RequestBody HealthRecordSaveDTO dto) {
        return ApiResult.success("健康记录已修改", healthRecordService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        healthRecordService.delete(id);
        return ApiResult.success("健康记录已删除", null);
    }
}
