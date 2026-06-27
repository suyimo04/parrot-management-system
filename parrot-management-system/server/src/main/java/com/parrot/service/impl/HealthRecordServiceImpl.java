package com.parrot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parrot.common.BusinessException;
import com.parrot.common.CurrentUserContext;
import com.parrot.common.PageResult;
import com.parrot.common.ResultCode;
import com.parrot.dto.HealthRecordQueryDTO;
import com.parrot.dto.HealthRecordSaveDTO;
import com.parrot.entity.HealthRecord;
import com.parrot.entity.Parrot;
import com.parrot.entity.SysUser;
import com.parrot.mapper.HealthRecordMapper;
import com.parrot.mapper.ParrotMapper;
import com.parrot.mapper.SysUserMapper;
import com.parrot.service.HealthRecordService;
import com.parrot.vo.HealthRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalTime;
import java.util.List;

/**
 * 健康记录实现，出现异常症状时同步更新鹦鹉健康状态。
 */
@Service
public class HealthRecordServiceImpl implements HealthRecordService {

    private final HealthRecordMapper healthRecordMapper;
    private final ParrotMapper parrotMapper;
    private final SysUserMapper sysUserMapper;

    public HealthRecordServiceImpl(HealthRecordMapper healthRecordMapper,
                                   ParrotMapper parrotMapper,
                                   SysUserMapper sysUserMapper) {
        this.healthRecordMapper = healthRecordMapper;
        this.parrotMapper = parrotMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public PageResult<HealthRecordVO> page(HealthRecordQueryDTO query) {
        Page<HealthRecord> page = new Page<>(safePage(query.getPage()), safeSize(query.getSize()));
        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<HealthRecord>()
                .eq(query.getParrotId() != null, HealthRecord::getParrotId, query.getParrotId())
                .ge(query.getStartDate() != null, HealthRecord::getRecordDate, query.getStartDate())
                .le(query.getEndDate() != null, HealthRecord::getRecordDate, query.getEndDate())
                .orderByDesc(HealthRecord::getRecordDate)
                .orderByDesc(HealthRecord::getCreateTime);
        if (query.getHasAbnormal() != null) {
            if (query.getHasAbnormal() == 1) {
                wrapper.isNotNull(HealthRecord::getAbnormalSymptoms).ne(HealthRecord::getAbnormalSymptoms, "");
            } else {
                wrapper.and(w -> w.isNull(HealthRecord::getAbnormalSymptoms).or().eq(HealthRecord::getAbnormalSymptoms, ""));
            }
        }
        Page<HealthRecord> result = healthRecordMapper.selectPage(page, wrapper);
        List<HealthRecordVO> records = result.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(records, result.getTotal(), result.getSize(), result.getCurrent());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HealthRecordVO add(HealthRecordSaveDTO dto) {
        checkParrot(dto.getParrotId());
        HealthRecord record = new HealthRecord();
        BeanUtils.copyProperties(dto, record);
        record.setRecorderId(currentUserId());
        healthRecordMapper.insert(record);
        markAbnormal(dto);
        return toVO(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HealthRecordVO update(Long id, HealthRecordSaveDTO dto) {
        HealthRecord record = getRecord(id);
        checkParrot(dto.getParrotId());
        BeanUtils.copyProperties(dto, record);
        if (record.getRecorderId() == null) {
            record.setRecorderId(currentUserId());
        }
        healthRecordMapper.updateById(record);
        markAbnormal(dto);
        return toVO(record);
    }

    @Override
    public void delete(Long id) {
        getRecord(id);
        healthRecordMapper.deleteById(id);
    }

    private void markAbnormal(HealthRecordSaveDTO dto) {
        if (!StringUtils.hasText(dto.getAbnormalSymptoms())) {
            return;
        }
        Parrot parrot = parrotMapper.selectById(dto.getParrotId());
        parrot.setHealthStatus("观察中");
        parrotMapper.updateById(parrot);
    }

    private void checkParrot(Long id) {
        if (parrotMapper.selectById(id) == null) {
            throw new BusinessException("鹦鹉档案不存在");
        }
    }

    private HealthRecord getRecord(Long id) {
        HealthRecord record = healthRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("健康记录不存在");
        }
        return record;
    }

    private Long currentUserId() {
        Long userId = CurrentUserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        return userId;
    }

    private HealthRecordVO toVO(HealthRecord record) {
        HealthRecordVO vo = new HealthRecordVO();
        BeanUtils.copyProperties(record, vo);
        Parrot parrot = record.getParrotId() == null ? null : parrotMapper.selectById(record.getParrotId());
        SysUser user = record.getRecorderId() == null ? null : sysUserMapper.selectById(record.getRecorderId());
        vo.setParrotName(parrot == null ? null : parrot.getName());
        vo.setRecorderName(user == null ? null : user.getRealName());
        return vo;
    }

    private long safePage(Long page) {
        return page == null || page < 1 ? 1 : page;
    }

    private long safeSize(Long size) {
        return size == null || size < 1 ? 10 : Math.min(size, 100);
    }
}
