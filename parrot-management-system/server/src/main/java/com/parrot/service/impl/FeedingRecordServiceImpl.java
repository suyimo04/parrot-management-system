package com.parrot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parrot.common.BusinessException;
import com.parrot.common.CurrentUserContext;
import com.parrot.common.PageResult;
import com.parrot.common.ResultCode;
import com.parrot.dto.FeedingRecordQueryDTO;
import com.parrot.dto.FeedingRecordSaveDTO;
import com.parrot.entity.FeedingRecord;
import com.parrot.entity.Parrot;
import com.parrot.entity.SysUser;
import com.parrot.mapper.FeedingRecordMapper;
import com.parrot.mapper.ParrotMapper;
import com.parrot.mapper.SysUserMapper;
import com.parrot.service.FeedingRecordService;
import com.parrot.vo.FeedingRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 喂养记录实现，记录人默认取当前登录饲养员或管理员。
 */
@Service
public class FeedingRecordServiceImpl implements FeedingRecordService {

    private final FeedingRecordMapper feedingRecordMapper;
    private final ParrotMapper parrotMapper;
    private final SysUserMapper sysUserMapper;

    public FeedingRecordServiceImpl(FeedingRecordMapper feedingRecordMapper,
                                    ParrotMapper parrotMapper,
                                    SysUserMapper sysUserMapper) {
        this.feedingRecordMapper = feedingRecordMapper;
        this.parrotMapper = parrotMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public PageResult<FeedingRecordVO> page(FeedingRecordQueryDTO query) {
        Page<FeedingRecord> page = new Page<>(safePage(query.getPage()), safeSize(query.getSize()));
        LambdaQueryWrapper<FeedingRecord> wrapper = new LambdaQueryWrapper<FeedingRecord>()
                .eq(query.getParrotId() != null, FeedingRecord::getParrotId, query.getParrotId())
                .eq(query.getRecorderId() != null, FeedingRecord::getRecorderId, query.getRecorderId())
                .ge(query.getStartDate() != null, FeedingRecord::getFeedingDate, query.getStartDate())
                .le(query.getEndDate() != null, FeedingRecord::getFeedingDate, query.getEndDate())
                .orderByDesc(FeedingRecord::getFeedingDate)
                .orderByDesc(FeedingRecord::getCreateTime);
        Page<FeedingRecord> result = feedingRecordMapper.selectPage(page, wrapper);
        List<FeedingRecordVO> records = result.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(records, result.getTotal(), result.getSize(), result.getCurrent());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FeedingRecordVO add(FeedingRecordSaveDTO dto) {
        checkParrot(dto.getParrotId());
        FeedingRecord record = new FeedingRecord();
        BeanUtils.copyProperties(dto, record);
        record.setRecorderId(currentUserId());
        if (record.getIsFinished() == null) {
            record.setIsFinished(1);
        }
        feedingRecordMapper.insert(record);
        return toVO(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FeedingRecordVO update(Long id, FeedingRecordSaveDTO dto) {
        FeedingRecord record = getRecord(id);
        checkParrot(dto.getParrotId());
        BeanUtils.copyProperties(dto, record);
        if (record.getRecorderId() == null) {
            record.setRecorderId(currentUserId());
        }
        if (record.getIsFinished() == null) {
            record.setIsFinished(1);
        }
        feedingRecordMapper.updateById(record);
        return toVO(record);
    }

    @Override
    public void delete(Long id) {
        getRecord(id);
        feedingRecordMapper.deleteById(id);
    }

    private void checkParrot(Long id) {
        if (parrotMapper.selectById(id) == null) {
            throw new BusinessException("鹦鹉档案不存在");
        }
    }

    private FeedingRecord getRecord(Long id) {
        FeedingRecord record = feedingRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("喂养记录不存在");
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

    private FeedingRecordVO toVO(FeedingRecord record) {
        FeedingRecordVO vo = new FeedingRecordVO();
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
