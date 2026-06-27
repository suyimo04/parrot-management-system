package com.parrot.service;

import com.parrot.common.PageResult;
import com.parrot.dto.FeedingRecordQueryDTO;
import com.parrot.dto.FeedingRecordSaveDTO;
import com.parrot.vo.FeedingRecordVO;

/**
 * 喂养记录业务。
 */
public interface FeedingRecordService {

    PageResult<FeedingRecordVO> page(FeedingRecordQueryDTO query);

    FeedingRecordVO add(FeedingRecordSaveDTO dto);

    FeedingRecordVO update(Long id, FeedingRecordSaveDTO dto);

    void delete(Long id);
}
