package com.parrot.controller;

import com.parrot.common.ApiResult;
import com.parrot.common.PageResult;
import com.parrot.dto.AppointmentHandleDTO;
import com.parrot.dto.AppointmentQueryDTO;
import com.parrot.dto.AppointmentSubmitDTO;
import com.parrot.service.AppointmentService;
import com.parrot.vo.AppointmentVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台客户预约和后台预约处理接口。
 */
@RestController
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/api/portal/appointment")
    public ApiResult<AppointmentVO> submit(@Valid @RequestBody AppointmentSubmitDTO dto) {
        return ApiResult.success("预约提交成功", appointmentService.submit(dto));
    }

    @GetMapping("/api/portal/appointment/my")
    public ApiResult<List<AppointmentVO>> my(@RequestParam(required = false) String status) {
        return ApiResult.success(appointmentService.myList(status));
    }

    @PutMapping("/api/portal/appointment/{id}/cancel")
    public ApiResult<Void> cancelMy(@PathVariable Long id) {
        appointmentService.cancelMy(id);
        return ApiResult.success("预约已取消", null);
    }

    @GetMapping("/api/admin/appointment/page")
    public ApiResult<PageResult<AppointmentVO>> page(AppointmentQueryDTO query) {
        return ApiResult.success(appointmentService.page(query));
    }

    @PutMapping("/api/admin/appointment/{id}/confirm")
    public ApiResult<AppointmentVO> confirm(@PathVariable Long id, @RequestBody(required = false) AppointmentHandleDTO dto) {
        return ApiResult.success("预约已确认", appointmentService.confirm(id, dto));
    }

    @PutMapping({"/api/admin/appointment/{id}/complete", "/api/admin/appointment/{id}/finish"})
    public ApiResult<AppointmentVO> complete(@PathVariable Long id, @RequestBody AppointmentHandleDTO dto) {
        return ApiResult.success("预约已完成", appointmentService.complete(id, dto));
    }

    @PutMapping("/api/admin/appointment/{id}/reject")
    public ApiResult<AppointmentVO> reject(@PathVariable Long id, @RequestBody AppointmentHandleDTO dto) {
        return ApiResult.success("预约已驳回", appointmentService.reject(id, dto));
    }

    @PutMapping("/api/admin/appointment/{id}/cancel")
    public ApiResult<AppointmentVO> cancelByAdmin(@PathVariable Long id, @RequestBody(required = false) AppointmentHandleDTO dto) {
        return ApiResult.success("预约已取消", appointmentService.cancelByAdmin(id, dto));
    }
}
