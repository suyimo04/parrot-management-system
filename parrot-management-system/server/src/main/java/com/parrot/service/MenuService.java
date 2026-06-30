package com.parrot.service;

import com.parrot.dto.MenuAssignDTO;
import com.parrot.vo.MenuPermissionVO;
import com.parrot.vo.MenuVO;

import java.util.List;

/**
 * 菜单权限业务。
 */
public interface MenuService {

    List<MenuVO> tree();

    MenuPermissionVO currentMenus();

    List<Long> roleMenuIds(String role);

    List<Long> userMenuIds(Long userId);

    void assignRole(MenuAssignDTO dto);

    void assignUser(MenuAssignDTO dto);

    boolean canAccessApi(String method, String uri, Long userId, String role);
}
