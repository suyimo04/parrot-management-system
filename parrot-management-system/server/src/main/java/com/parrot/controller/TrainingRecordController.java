package com.parrot.controller;

import com.parrot.common.ApiResult;
import com.parrot.common.PageResult;
import com.parrot.dto.TrainingRecordQueryDTO;
import com.parrot.dto.TrainingRecordSaveDTO;
import com.parrot.service.TrainingRecordService;
import com.parrot.vo.TrainingRecordVO;
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
 * 后台训练记录接口。
 */
@RestController
@RequestMapping("/api/admin/training-record")
public class TrainingRecordController {

    private final TrainingRecordService trainingRecordService;

    public TrainingRecordController(TrainingRecordService trainingRecordService) {
        this.trainingRecordService = trainingRecordService;
    }

    @GetMapping("/page")
    public ApiResult<PageResult<TrainingRecordVO>> page(TrainingRecordQueryDTO query) {
        return ApiResult.success(trainingRecordService.page(query));
    }

    @PostMapping
    public ApiResult<TrainingRecordVO> add(@Valid @RequestBody TrainingRecordSaveDTO dto) {
        return ApiResult.success("训练记录已保存", trainingRecordService.add(dto));
    }

    @PutMapping("/{id}")
    public ApiResult<TrainingRecordVO> update(@PathVariable Long id, @Valid @RequestBody TrainingRecordSaveDTO dto) {
        return ApiResult.success("训练记录已修改", trainingRecordService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        trainingRecordService.delete(id);
        return ApiResult.success("训练记录已删除", null);
    }
}
