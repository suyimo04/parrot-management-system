package com.parrot.service;

import com.parrot.common.PageResult;
import com.parrot.dto.ParrotQueryDTO;
import com.parrot.dto.ParrotSaveDTO;
import com.parrot.vo.ParrotVO;

import java.util.List;

/**
 * 鹦鹉档案业务。
 */
public interface ParrotService {

    PageResult<ParrotVO> page(ParrotQueryDTO query);

    ParrotVO add(ParrotSaveDTO dto);

    ParrotVO update(Long id, ParrotSaveDTO dto);

    void delete(Long id);

    List<ParrotVO> publicList(ParrotQueryDTO query);

    ParrotVO publicDetail(Long id);
}
