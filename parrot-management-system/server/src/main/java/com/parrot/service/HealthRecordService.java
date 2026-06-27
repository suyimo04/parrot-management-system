package com.parrot.service;

import com.parrot.common.PageResult;
import com.parrot.dto.HealthRecordQueryDTO;
import com.parrot.dto.HealthRecordSaveDTO;
import com.parrot.vo.HealthRecordVO;

/**
 * 健康记录业务。
 */
public interface HealthRecordService {

    PageResult<HealthRecordVO> page(HealthRecordQueryDTO query);

    HealthRecordVO add(HealthRecordSaveDTO dto);

    HealthRecordVO update(Long id, HealthRecordSaveDTO dto);

    void delete(Long id);
}
