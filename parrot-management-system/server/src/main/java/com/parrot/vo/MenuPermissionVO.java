package com.parrot.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 当前用户可用菜单，登录后前端用它生成左侧菜单。
 */
@Data
public class MenuPermissionVO {

    private List<String> codes = new ArrayList<>();
    private List<MenuVO> menus = new ArrayList<>();
}
