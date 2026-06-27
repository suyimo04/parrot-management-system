package com.parrot.controller;

import com.parrot.common.ApiResult;
import com.parrot.common.PageResult;
import com.parrot.dto.FeedingRecordQueryDTO;
import com.parrot.dto.FeedingRecordSaveDTO;
import com.parrot.service.FeedingRecordService;
import com.parrot.vo.FeedingRecordVO;
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
 * 后台喂养记录接口。
 */
@RestController
@RequestMapping("/api/admin/feeding-record")
public class FeedingRecordController {

    private final FeedingRecordService feedingRecordService;

    public FeedingRecordController(FeedingRecordService feedingRecordService) {
        this.feedingRecordService = feedingRecordService;
    }

    @GetMapping("/page")
    public ApiResult<PageResult<FeedingRecordVO>> page(FeedingRecordQueryDTO query) {
        return ApiResult.success(feedingRecordService.page(query));
    }

    @PostMapping
    public ApiResult<FeedingRecordVO> add(@Valid @RequestBody FeedingRecordSaveDTO dto) {
        return ApiResult.success("喂养记录已保存", feedingRecordService.add(dto));
    }

    @PutMapping("/{id}")
    public ApiResult<FeedingRecordVO> update(@PathVariable Long id, @Valid @RequestBody FeedingRecordSaveDTO dto) {
        return ApiResult.success("喂养记录已修改", feedingRecordService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        feedingRecordService.delete(id);
        return ApiResult.success("喂养记录已删除", null);
    }
}
