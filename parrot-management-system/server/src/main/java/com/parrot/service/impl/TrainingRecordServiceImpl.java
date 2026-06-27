package com.parrot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parrot.common.BusinessException;
import com.parrot.common.CurrentUserContext;
import com.parrot.common.PageResult;
import com.parrot.common.ResultCode;
import com.parrot.dto.TrainingRecordQueryDTO;
import com.parrot.dto.TrainingRecordSaveDTO;
import com.parrot.entity.Parrot;
import com.parrot.entity.SysUser;
import com.parrot.entity.TrainingRecord;
import com.parrot.mapper.ParrotMapper;
import com.parrot.mapper.SysUserMapper;
import com.parrot.mapper.TrainingRecordMapper;
import com.parrot.service.TrainingRecordService;
import com.parrot.vo.TrainingRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 训练记录实现。
 */
@Service
public class TrainingRecordServiceImpl implements TrainingRecordService {

    private final TrainingRecordMapper trainingRecordMapper;
    private final ParrotMapper parrotMapper;
    private final SysUserMapper sysUserMapper;

    public TrainingRecordServiceImpl(TrainingRecordMapper trainingRecordMapper,
                                     ParrotMapper parrotMapper,
                                     SysUserMapper sysUserMapper) {
        this.trainingRecordMapper = trainingRecordMapper;
        this.parrotMapper = parrotMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public PageResult<TrainingRecordVO> page(TrainingRecordQueryDTO query) {
        Page<TrainingRecord> page = new Page<>(safePage(query.getPage()), safeSize(query.getSize()));
        LambdaQueryWrapper<TrainingRecord> wrapper = new LambdaQueryWrapper<TrainingRecord>()
                .eq(query.getParrotId() != null, TrainingRecord::getParrotId, query.getParrotId())
                .like(StringUtils.hasText(query.getProject()), TrainingRecord::getProject, query.getProject())
                .ge(query.getStartDate() != null, TrainingRecord::getTrainingDate, query.getStartDate())
                .le(query.getEndDate() != null, TrainingRecord::getTrainingDate, query.getEndDate())
                .orderByDesc(TrainingRecord::getTrainingDate)
                .orderByDesc(TrainingRecord::getCreateTime);
        Page<TrainingRecord> result = trainingRecordMapper.selectPage(page, wrapper);
        List<TrainingRecordVO> records = result.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(records, result.getTotal(), result.getSize(), result.getCurrent());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TrainingRecordVO add(TrainingRecordSaveDTO dto) {
        checkParrot(dto.getParrotId());
        TrainingRecord record = new TrainingRecord();
        BeanUtils.copyProperties(dto, record);
        record.setRecorderId(currentUserId());
        trainingRecordMapper.insert(record);
        return toVO(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TrainingRecordVO update(Long id, TrainingRecordSaveDTO dto) {
        TrainingRecord record = getRecord(id);
        checkParrot(dto.getParrotId());
        BeanUtils.copyProperties(dto, record);
        if (record.getRecorderId() == null) {
            record.setRecorderId(currentUserId());
        }
        trainingRecordMapper.updateById(record);
        return toVO(record);
    }

    @Override
    public void delete(Long id) {
        getRecord(id);
        trainingRecordMapper.deleteById(id);
    }

    private void checkParrot(Long id) {
        if (parrotMapper.selectById(id) == null) {
            throw new BusinessException("鹦鹉档案不存在");
        }
    }

    private TrainingRecord getRecord(Long id) {
        TrainingRecord record = trainingRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("训练记录不存在");
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

    private TrainingRecordVO toVO(TrainingRecord record) {
        TrainingRecordVO vo = new TrainingRecordVO();
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
