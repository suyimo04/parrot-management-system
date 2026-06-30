package com.parrot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.parrot.entity.SysUserMenu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户菜单关系表操作。
 */
@Mapper
public interface SysUserMenuMapper extends BaseMapper<SysUserMenu> {
}
