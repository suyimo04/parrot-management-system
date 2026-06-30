package com.parrot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.parrot.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜单表基础操作。
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {
}
