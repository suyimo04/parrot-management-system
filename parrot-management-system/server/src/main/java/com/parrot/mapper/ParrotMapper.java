package com.parrot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.parrot.entity.Parrot;
import org.apache.ibatis.annotations.Mapper;

/**
 * 鹦鹉档案 Mapper。
 */
@Mapper
public interface ParrotMapper extends BaseMapper<Parrot> {
}
