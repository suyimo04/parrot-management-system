package com.parrot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 系统菜单，页面和一些主要功能按钮都按菜单编码来控制。
 */
@Data
@TableName("sys_menu")
public class SysMenu {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parentId;

    private String menuCode;

    private String menuName;

    private String menuType;

    private String path;

    private String apiPattern;

    private String icon;

    private Integer sortNo;

    private Integer status;
}
