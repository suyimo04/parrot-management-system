package com.parrot.service;

import com.parrot.common.PageResult;
import com.parrot.dto.SpeciesQueryDTO;
import com.parrot.dto.SpeciesSaveDTO;
import com.parrot.vo.SpeciesVO;

import java.util.List;

/**
 * 鹦鹉品种业务。
 */
public interface SpeciesService {

    PageResult<SpeciesVO> page(SpeciesQueryDTO query);

    List<SpeciesVO> listEnabled();

    SpeciesVO add(SpeciesSaveDTO dto);

    SpeciesVO update(Long id, SpeciesSaveDTO dto);

    void delete(Long id);
}
