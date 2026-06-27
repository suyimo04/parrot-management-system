package com.parrot.controller;

import com.parrot.common.ApiResult;
import com.parrot.common.PageResult;
import com.parrot.dto.NoticeQueryDTO;
import com.parrot.dto.NoticeSaveDTO;
import com.parrot.service.NoticeService;
import com.parrot.vo.NoticeVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 公告知识接口，后台维护，前台只读已发布内容。
 */
@RestController
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping("/api/admin/notice/page")
    public ApiResult<PageResult<NoticeVO>> page(NoticeQueryDTO query) {
        return ApiResult.success(noticeService.page(query));
    }

    @PostMapping("/api/admin/notice")
    public ApiResult<NoticeVO> add(@Valid @RequestBody NoticeSaveDTO dto) {
        return ApiResult.success("公告已保存", noticeService.add(dto));
    }

    @PutMapping("/api/admin/notice/{id}")
    public ApiResult<NoticeVO> update(@PathVariable Long id, @Valid @RequestBody NoticeSaveDTO dto) {
        return ApiResult.success("公告已修改", noticeService.update(id, dto));
    }

    @DeleteMapping("/api/admin/notice/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return ApiResult.success("公告已删除", null);
    }

    @GetMapping("/api/public/notice/list")
    public ApiResult<List<NoticeVO>> publicList(NoticeQueryDTO query) {
        return ApiResult.success(noticeService.publicList(query));
    }

    @GetMapping("/api/public/notice/{id}")
    public ApiResult<NoticeVO> publicDetail(@PathVariable Long id) {
        return ApiResult.success(noticeService.publicDetail(id));
    }
}
