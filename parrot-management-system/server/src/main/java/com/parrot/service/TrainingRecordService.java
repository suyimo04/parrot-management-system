package com.parrot.service;

import com.parrot.common.PageResult;
import com.parrot.dto.TrainingRecordQueryDTO;
import com.parrot.dto.TrainingRecordSaveDTO;
import com.parrot.vo.TrainingRecordVO;

/**
 * 训练记录业务。
 */
public interface TrainingRecordService {

    PageResult<TrainingRecordVO> page(TrainingRecordQueryDTO query);

    TrainingRecordVO add(TrainingRecordSaveDTO dto);

    TrainingRecordVO update(Long id, TrainingRecordSaveDTO dto);

    void delete(Long id);
}
