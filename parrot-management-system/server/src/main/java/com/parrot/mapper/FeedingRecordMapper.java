package com.parrot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.parrot.entity.FeedingRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 喂养记录 Mapper。
 */
@Mapper
public interface FeedingRecordMapper extends BaseMapper<FeedingRecord> {
}
