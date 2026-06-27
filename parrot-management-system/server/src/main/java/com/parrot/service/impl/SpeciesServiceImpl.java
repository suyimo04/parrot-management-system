package com.parrot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parrot.common.BusinessException;
import com.parrot.common.PageResult;
import com.parrot.dto.SpeciesQueryDTO;
import com.parrot.dto.SpeciesSaveDTO;
import com.parrot.entity.Parrot;
import com.parrot.entity.ParrotSpecies;
import com.parrot.mapper.ParrotMapper;
import com.parrot.mapper.ParrotSpeciesMapper;
import com.parrot.service.SpeciesService;
import com.parrot.vo.SpeciesVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 品种管理实现，逻辑尽量直白，方便后续页面联调。
 */
@Service
public class SpeciesServiceImpl implements SpeciesService {

    private final ParrotSpeciesMapper speciesMapper;
    private final ParrotMapper parrotMapper;

    public SpeciesServiceImpl(ParrotSpeciesMapper speciesMapper, ParrotMapper parrotMapper) {
        this.speciesMapper = speciesMapper;
        this.parrotMapper = parrotMapper;
    }

    @Override
    public PageResult<SpeciesVO> page(SpeciesQueryDTO query) {
        Page<ParrotSpecies> page = new Page<>(safePage(query.getPage()), safeSize(query.getSize()));
        LambdaQueryWrapper<ParrotSpecies> wrapper = new LambdaQueryWrapper<ParrotSpecies>()
                .like(StringUtils.hasText(query.getName()), ParrotSpecies::getName, query.getName())
                .eq(query.getStatus() != null, ParrotSpecies::getStatus, query.getStatus())
                .orderByDesc(ParrotSpecies::getCreateTime);
        Page<ParrotSpecies> result = speciesMapper.selectPage(page, wrapper);
        List<SpeciesVO> records = result.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(records, result.getTotal(), result.getSize(), result.getCurrent());
    }

    @Override
    public List<SpeciesVO> listEnabled() {
        return speciesMapper.selectList(new LambdaQueryWrapper<ParrotSpecies>()
                        .eq(ParrotSpecies::getStatus, 1)
                        .orderByAsc(ParrotSpecies::getName))
                .stream()
                .map(this::toVO)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SpeciesVO add(SpeciesSaveDTO dto) {
        checkStatus(dto.getStatus());
        ParrotSpecies species = new ParrotSpecies();
        BeanUtils.copyProperties(dto, species);
        speciesMapper.insert(species);
        return toVO(species);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SpeciesVO update(Long id, SpeciesSaveDTO dto) {
        checkStatus(dto.getStatus());
        ParrotSpecies species = getSpecies(id);
        BeanUtils.copyProperties(dto, species);
        speciesMapper.updateById(species);
        return toVO(species);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ParrotSpecies species = getSpecies(id);
        Long used = parrotMapper.selectCount(new LambdaQueryWrapper<Parrot>()
                .eq(Parrot::getSpeciesId, id));
        if (used != null && used > 0) {
            // 已被档案引用时不硬删，改成禁用更适合演示数据保留。
            species.setStatus(0);
            speciesMapper.updateById(species);
            return;
        }
        speciesMapper.deleteById(id);
    }

    private ParrotSpecies getSpecies(Long id) {
        ParrotSpecies species = speciesMapper.selectById(id);
        if (species == null) {
            throw new BusinessException("品种不存在");
        }
        return species;
    }

    private void checkStatus(Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("品种状态不正确");
        }
    }

    private long safePage(Long page) {
        return page == null || page < 1 ? 1 : page;
    }

    private long safeSize(Long size) {
        return size == null || size < 1 ? 10 : Math.min(size, 100);
    }

    private SpeciesVO toVO(ParrotSpecies species) {
        SpeciesVO vo = new SpeciesVO();
        BeanUtils.copyProperties(species, vo);
        return vo;
    }
}
