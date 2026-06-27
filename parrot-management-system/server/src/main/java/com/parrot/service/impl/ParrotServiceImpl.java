package com.parrot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parrot.common.BusinessException;
import com.parrot.common.PageResult;
import com.parrot.dto.ParrotQueryDTO;
import com.parrot.dto.ParrotSaveDTO;
import com.parrot.entity.Appointment;
import com.parrot.entity.FeedingRecord;
import com.parrot.entity.HealthRecord;
import com.parrot.entity.Parrot;
import com.parrot.entity.ParrotSpecies;
import com.parrot.entity.TrainingRecord;
import com.parrot.mapper.AppointmentMapper;
import com.parrot.mapper.FeedingRecordMapper;
import com.parrot.mapper.HealthRecordMapper;
import com.parrot.mapper.ParrotMapper;
import com.parrot.mapper.ParrotSpeciesMapper;
import com.parrot.mapper.TrainingRecordMapper;
import com.parrot.service.ParrotService;
import com.parrot.vo.ParrotVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 鹦鹉档案实现，公开接口和后台接口共用转换逻辑。
 */
@Service
public class ParrotServiceImpl implements ParrotService {

    private static final Set<String> HEALTH_STATUS = Set.of("正常", "观察中", "治疗中", "已预约", "已停用");

    private final ParrotMapper parrotMapper;
    private final ParrotSpeciesMapper speciesMapper;
    private final HealthRecordMapper healthRecordMapper;
    private final FeedingRecordMapper feedingRecordMapper;
    private final TrainingRecordMapper trainingRecordMapper;
    private final AppointmentMapper appointmentMapper;

    public ParrotServiceImpl(ParrotMapper parrotMapper,
                             ParrotSpeciesMapper speciesMapper,
                             HealthRecordMapper healthRecordMapper,
                             FeedingRecordMapper feedingRecordMapper,
                             TrainingRecordMapper trainingRecordMapper,
                             AppointmentMapper appointmentMapper) {
        this.parrotMapper = parrotMapper;
        this.speciesMapper = speciesMapper;
        this.healthRecordMapper = healthRecordMapper;
        this.feedingRecordMapper = feedingRecordMapper;
        this.trainingRecordMapper = trainingRecordMapper;
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    public PageResult<ParrotVO> page(ParrotQueryDTO query) {
        Page<Parrot> page = new Page<>(safePage(query.getPage()), safeSize(query.getSize()));
        Page<Parrot> result = parrotMapper.selectPage(page, buildWrapper(query, false));
        List<ParrotVO> records = toVOList(result.getRecords());
        return new PageResult<>(records, result.getTotal(), result.getSize(), result.getCurrent());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ParrotVO add(ParrotSaveDTO dto) {
        checkSave(dto, null);
        Parrot parrot = new Parrot();
        BeanUtils.copyProperties(dto, parrot);
        parrotMapper.insert(parrot);
        return toVO(parrot);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ParrotVO update(Long id, ParrotSaveDTO dto) {
        Parrot parrot = getParrot(id);
        checkSave(dto, id);
        BeanUtils.copyProperties(dto, parrot);
        parrotMapper.updateById(parrot);
        return toVO(parrot);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Parrot parrot = getParrot(id);
        Long activeAppointment = appointmentMapper.selectCount(new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getParrotId, id)
                .in(Appointment::getStatus, "待处理", "已确认"));
        if (activeAppointment != null && activeAppointment > 0) {
            throw new BusinessException("该鹦鹉还有未完成预约，暂不能停用");
        }

        if (hasCareRecord(id)) {
            parrot.setHealthStatus("已停用");
            parrot.setCurrentStatus("已停用");
            parrot.setIsPublic(0);
            parrotMapper.updateById(parrot);
            return;
        }
        parrotMapper.deleteById(id);
    }

    @Override
    public List<ParrotVO> publicList(ParrotQueryDTO query) {
        List<Parrot> parrots = parrotMapper.selectList(buildWrapper(query, true));
        return toVOList(parrots);
    }

    @Override
    public ParrotVO publicDetail(Long id) {
        Parrot parrot = parrotMapper.selectById(id);
        if (parrot == null || parrot.getIsPublic() == null || parrot.getIsPublic() != 1
                || !"正常".equals(parrot.getHealthStatus()) || "已停用".equals(parrot.getCurrentStatus())) {
            throw new BusinessException("鹦鹉信息不存在或暂未公开");
        }
        return toVO(parrot);
    }

    private LambdaQueryWrapper<Parrot> buildWrapper(ParrotQueryDTO query, boolean onlyPublic) {
        LambdaQueryWrapper<Parrot> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(Parrot::getCode, query.getKeyword())
                    .or()
                    .like(Parrot::getName, query.getKeyword()));
        }
        wrapper.eq(query.getSpeciesId() != null, Parrot::getSpeciesId, query.getSpeciesId())
                .eq(StringUtils.hasText(query.getHealthStatus()), Parrot::getHealthStatus, query.getHealthStatus())
                .eq(query.getIsPublic() != null, Parrot::getIsPublic, query.getIsPublic());
        if (onlyPublic) {
            wrapper.eq(Parrot::getIsPublic, 1)
                    .eq(Parrot::getHealthStatus, "正常")
                    .ne(Parrot::getCurrentStatus, "已停用");
        }
        return wrapper.orderByDesc(Parrot::getCreateTime);
    }

    private void checkSave(ParrotSaveDTO dto, Long id) {
        if (!HEALTH_STATUS.contains(dto.getHealthStatus())) {
            throw new BusinessException("健康状态不正确");
        }
        if (dto.getIsPublic() != 0 && dto.getIsPublic() != 1) {
            throw new BusinessException("公开状态不正确");
        }
        ParrotSpecies species = speciesMapper.selectById(dto.getSpeciesId());
        if (species == null || species.getStatus() == null || species.getStatus() == 0) {
            throw new BusinessException("请选择启用中的品种");
        }
        Parrot sameCode = parrotMapper.selectOne(new LambdaQueryWrapper<Parrot>()
                .eq(Parrot::getCode, dto.getCode())
                .last("limit 1"));
        if (sameCode != null && !Objects.equals(sameCode.getId(), id)) {
            throw new BusinessException("档案编号已存在");
        }
    }

    private boolean hasCareRecord(Long parrotId) {
        Long health = healthRecordMapper.selectCount(new LambdaQueryWrapper<HealthRecord>()
                .eq(HealthRecord::getParrotId, parrotId));
        Long feeding = feedingRecordMapper.selectCount(new LambdaQueryWrapper<FeedingRecord>()
                .eq(FeedingRecord::getParrotId, parrotId));
        Long training = trainingRecordMapper.selectCount(new LambdaQueryWrapper<TrainingRecord>()
                .eq(TrainingRecord::getParrotId, parrotId));
        return countPositive(health) || countPositive(feeding) || countPositive(training);
    }

    private boolean countPositive(Long count) {
        return count != null && count > 0;
    }

    private Parrot getParrot(Long id) {
        Parrot parrot = parrotMapper.selectById(id);
        if (parrot == null) {
            throw new BusinessException("鹦鹉档案不存在");
        }
        return parrot;
    }

    private List<ParrotVO> toVOList(List<Parrot> parrots) {
        if (parrots == null || parrots.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> speciesIds = parrots.stream()
                .map(Parrot::getSpeciesId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, ParrotSpecies> speciesMap = speciesIds.isEmpty()
                ? Map.of()
                : speciesMapper.selectBatchIds(speciesIds).stream()
                .collect(Collectors.toMap(ParrotSpecies::getId, Function.identity()));
        return parrots.stream().map(parrot -> toVO(parrot, speciesMap.get(parrot.getSpeciesId()))).toList();
    }

    private ParrotVO toVO(Parrot parrot) {
        ParrotSpecies species = parrot.getSpeciesId() == null ? null : speciesMapper.selectById(parrot.getSpeciesId());
        return toVO(parrot, species);
    }

    private ParrotVO toVO(Parrot parrot, ParrotSpecies species) {
        ParrotVO vo = new ParrotVO();
        BeanUtils.copyProperties(parrot, vo);
        if (species != null) {
            vo.setSpeciesName(species.getName());
            vo.setSpeciesEnglishName(species.getEnglishName());
            vo.setSpeciesOrigin(species.getOrigin());
            vo.setSpeciesSize(species.getSize());
            vo.setSpeciesAvgLifespan(species.getAvgLifespan());
            vo.setSpeciesHabits(species.getHabits());
            vo.setSpeciesDifficulty(species.getDifficulty());
        }
        return vo;
    }

    private long safePage(Long page) {
        return page == null || page < 1 ? 1 : page;
    }

    private long safeSize(Long size) {
        return size == null || size < 1 ? 10 : Math.min(size, 100);
    }
}
