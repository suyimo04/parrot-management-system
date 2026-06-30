package com.parrot.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 前端菜单树展示对象。
 */
@Data
public class MenuVO {

    private Long id;
    private Long parentId;
    private String menuCode;
    private String menuName;
    private String menuType;
    private String path;
    private String icon;
    private Integer sortNo;
    private Integer status;
    private List<MenuVO> children = new ArrayList<>();
}
