package com.parrot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parrot.common.BusinessException;
import com.parrot.common.CurrentUserContext;
import com.parrot.common.PageResult;
import com.parrot.common.ResultCode;
import com.parrot.dto.AppointmentHandleDTO;
import com.parrot.dto.AppointmentQueryDTO;
import com.parrot.dto.AppointmentSubmitDTO;
import com.parrot.entity.Appointment;
import com.parrot.entity.Parrot;
import com.parrot.entity.SysUser;
import com.parrot.mapper.AppointmentMapper;
import com.parrot.mapper.ParrotMapper;
import com.parrot.mapper.SysUserMapper;
import com.parrot.service.AppointmentService;
import com.parrot.vo.AppointmentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约咨询实现，把状态流转写清楚，方便演示时说明。
 */
@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentMapper appointmentMapper;
    private final ParrotMapper parrotMapper;
    private final SysUserMapper sysUserMapper;

    public AppointmentServiceImpl(AppointmentMapper appointmentMapper,
                                  ParrotMapper parrotMapper,
                                  SysUserMapper sysUserMapper) {
        this.appointmentMapper = appointmentMapper;
        this.parrotMapper = parrotMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppointmentVO submit(AppointmentSubmitDTO dto) {
        Parrot parrot = parrotMapper.selectById(dto.getParrotId());
        if (parrot == null || parrot.getIsPublic() == null || parrot.getIsPublic() != 1
                || !"正常".equals(parrot.getHealthStatus()) || "已停用".equals(parrot.getCurrentStatus())) {
            throw new BusinessException("该鹦鹉暂不能预约");
        }
        if (!StringUtils.hasText(dto.getPhone()) && !StringUtils.hasText(dto.getEmail())) {
            throw new BusinessException("请至少填写一种联系方式");
        }

        Appointment appointment = new Appointment();
        BeanUtils.copyProperties(dto, appointment);
        appointment.setCustomerId(currentUserId());
        appointment.setStatus("待处理");
        appointmentMapper.insert(appointment);
        return toVO(appointment);
    }

    @Override
    public List<AppointmentVO> myList(String status) {
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getCustomerId, currentUserId())
                .eq(StringUtils.hasText(status), Appointment::getStatus, status)
                .orderByDesc(Appointment::getCreateTime);
        return appointmentMapper.selectList(wrapper).stream().map(this::toVO).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelMy(Long id) {
        Appointment appointment = getAppointment(id);
        if (!currentUserId().equals(appointment.getCustomerId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只能取消自己的预约");
        }
        if (!"待处理".equals(appointment.getStatus()) && !"已确认".equals(appointment.getStatus())) {
            throw new BusinessException("当前状态不能取消预约");
        }
        appointment.setStatus("已取消");
        appointmentMapper.updateById(appointment);
    }

    @Override
    public PageResult<AppointmentVO> page(AppointmentQueryDTO query) {
        Page<Appointment> page = new Page<>(safePage(query.getPage()), safeSize(query.getSize()));
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<Appointment>()
                .eq(StringUtils.hasText(query.getStatus()), Appointment::getStatus, query.getStatus())
                .eq(query.getParrotId() != null, Appointment::getParrotId, query.getParrotId())
                .ge(query.getStartDate() != null, Appointment::getAppointmentDate, query.getStartDate())
                .le(query.getEndDate() != null, Appointment::getAppointmentDate, query.getEndDate())
                .orderByDesc(Appointment::getCreateTime);
        addCustomerFilter(wrapper, query.getCustomerKeyword());
        Page<Appointment> result = appointmentMapper.selectPage(page, wrapper);
        List<AppointmentVO> records = result.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(records, result.getTotal(), result.getSize(), result.getCurrent());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppointmentVO confirm(Long id, AppointmentHandleDTO dto) {
        Appointment appointment = getAppointment(id);
        if (!"待处理".equals(appointment.getStatus())) {
            throw new BusinessException("只有待处理预约可以确认");
        }
        return handle(appointment, "已确认", dto, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppointmentVO complete(Long id, AppointmentHandleDTO dto) {
        Appointment appointment = getAppointment(id);
        if (!"已确认".equals(appointment.getStatus())) {
            throw new BusinessException("只有已确认预约可以完成");
        }
        if (dto == null || !StringUtils.hasText(dto.getResult())) {
            throw new BusinessException("完成预约时请填写处理结果");
        }
        return handle(appointment, "已完成", dto, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppointmentVO reject(Long id, AppointmentHandleDTO dto) {
        Appointment appointment = getAppointment(id);
        if (!"待处理".equals(appointment.getStatus())) {
            throw new BusinessException("只有待处理预约可以驳回");
        }
        if (dto == null || !StringUtils.hasText(dto.getResult())) {
            throw new BusinessException("驳回预约时请填写原因");
        }
        return handle(appointment, "已驳回", dto, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppointmentVO cancelByAdmin(Long id, AppointmentHandleDTO dto) {
        Appointment appointment = getAppointment(id);
        if (!"待处理".equals(appointment.getStatus()) && !"已确认".equals(appointment.getStatus())) {
            throw new BusinessException("当前状态不能取消预约");
        }
        return handle(appointment, "已取消", dto, true);
    }

    private AppointmentVO handle(Appointment appointment, String status, AppointmentHandleDTO dto, boolean remarkAsResult) {
        appointment.setStatus(status);
        appointment.setHandlerId(currentUserId());
        appointment.setHandleTime(LocalDateTime.now());
        if (dto != null) {
            appointment.setResult(dto.getResult());
            appointment.setBackendRemark(dto.getBackendRemark());
            if (remarkAsResult && !StringUtils.hasText(dto.getResult()) && StringUtils.hasText(dto.getBackendRemark())) {
                appointment.setResult(dto.getBackendRemark());
            }
        }
        appointmentMapper.updateById(appointment);
        return toVO(appointment);
    }

    private void addCustomerFilter(LambdaQueryWrapper<Appointment> wrapper, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return;
        }
        List<Long> ids = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                        .like(SysUser::getUsername, keyword)
                        .or()
                        .like(SysUser::getRealName, keyword))
                .stream()
                .map(SysUser::getId)
                .toList();
        if (ids.isEmpty()) {
            wrapper.eq(Appointment::getCustomerId, -1L);
        } else {
            wrapper.in(Appointment::getCustomerId, ids);
        }
    }

    private Appointment getAppointment(Long id) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw new BusinessException("预约记录不存在");
        }
        if ("已完成".equals(appointment.getStatus()) || "已取消".equals(appointment.getStatus())
                || "已驳回".equals(appointment.getStatus())) {
            return appointment;
        }
        return appointment;
    }

    private Long currentUserId() {
        Long userId = CurrentUserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        return userId;
    }

    private AppointmentVO toVO(Appointment appointment) {
        AppointmentVO vo = new AppointmentVO();
        BeanUtils.copyProperties(appointment, vo);
        SysUser customer = appointment.getCustomerId() == null ? null : sysUserMapper.selectById(appointment.getCustomerId());
        SysUser handler = appointment.getHandlerId() == null ? null : sysUserMapper.selectById(appointment.getHandlerId());
        Parrot parrot = appointment.getParrotId() == null ? null : parrotMapper.selectById(appointment.getParrotId());
        vo.setCustomerName(customer == null ? null : customer.getRealName());
        vo.setHandlerName(handler == null ? null : handler.getRealName());
        vo.setParrotName(parrot == null ? null : parrot.getName());
        return vo;
    }

    private long safePage(Long page) {
        return page == null || page < 1 ? 1 : page;
    }

    private long safeSize(Long size) {
        return size == null || size < 1 ? 10 : Math.min(size, 100);
    }
}
