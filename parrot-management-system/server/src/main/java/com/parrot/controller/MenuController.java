package com.parrot.controller;

import com.parrot.common.ApiResult;
import com.parrot.dto.MenuAssignDTO;
import com.parrot.service.MenuService;
import com.parrot.vo.MenuPermissionVO;
import com.parrot.vo.MenuVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 菜单管理接口，管理员可在前端配置不同身份或用户能访问的功能。
 */
@RestController
@RequestMapping("/api/admin/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/tree")
    public ApiResult<List<MenuVO>> tree() {
        return ApiResult.success(menuService.tree());
    }

    @GetMapping("/current")
    public ApiResult<MenuPermissionVO> current() {
        return ApiResult.success(menuService.currentMenus());
    }

    @GetMapping("/role/{role}")
    public ApiResult<List<Long>> roleMenus(@PathVariable String role) {
        return ApiResult.success(menuService.roleMenuIds(role));
    }

    @PutMapping("/role")
    public ApiResult<Void> assignRole(@Valid @RequestBody MenuAssignDTO dto) {
        menuService.assignRole(dto);
        return ApiResult.success("身份菜单已保存", null);
    }

    @GetMapping("/user/{userId}")
    public ApiResult<List<Long>> userMenus(@PathVariable Long userId) {
        return ApiResult.success(menuService.userMenuIds(userId));
    }

    @PutMapping("/user")
    public ApiResult<Void> assignUser(@Valid @RequestBody MenuAssignDTO dto) {
        menuService.assignUser(dto);
        return ApiResult.success("用户菜单已保存", null);
    }
}
