package com.parrot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.parrot.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表基础操作，复杂查询后面模块再按需补。
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
