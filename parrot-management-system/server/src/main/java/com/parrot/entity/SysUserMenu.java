package com.parrot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户额外菜单关系，处理个别用户单独放权的情况。
 */
@Data
@TableName("sys_user_menu")
public class SysUserMenu {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long menuId;
}
