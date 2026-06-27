package com.parrot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.parrot.entity.HealthRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 健康记录 Mapper。
 */
@Mapper
public interface HealthRecordMapper extends BaseMapper<HealthRecord> {
}
