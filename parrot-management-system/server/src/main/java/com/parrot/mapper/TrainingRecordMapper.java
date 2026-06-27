package com.parrot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.parrot.entity.TrainingRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 训练记录 Mapper。
 */
@Mapper
public interface TrainingRecordMapper extends BaseMapper<TrainingRecord> {
}
