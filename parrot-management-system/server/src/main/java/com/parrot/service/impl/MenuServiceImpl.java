package com.parrot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.parrot.common.BusinessException;
import com.parrot.common.CurrentUserContext;
import com.parrot.dto.MenuAssignDTO;
import com.parrot.entity.SysMenu;
import com.parrot.entity.SysRoleMenu;
import com.parrot.entity.SysUserMenu;
import com.parrot.mapper.SysMenuMapper;
import com.parrot.mapper.SysRoleMenuMapper;
import com.parrot.mapper.SysUserMenuMapper;
import com.parrot.service.MenuService;
import com.parrot.vo.MenuPermissionVO;
import com.parrot.vo.MenuVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 菜单权限实现，角色授权和用户授权最后会合并成一份可访问菜单。
 */
@Service
public class MenuServiceImpl implements MenuService {

    private static final Set<String> ROLE_SET = Set.of("ADMIN", "KEEPER", "CUSTOMER");

    private final SysMenuMapper menuMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysUserMenuMapper userMenuMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public MenuServiceImpl(SysMenuMapper menuMapper,
                           SysRoleMenuMapper roleMenuMapper,
                           SysUserMenuMapper userMenuMapper) {
        this.menuMapper = menuMapper;
        this.roleMenuMapper = roleMenuMapper;
        this.userMenuMapper = userMenuMapper;
    }

    @Override
    public List<MenuVO> tree() {
        return buildTree(listAllMenus());
    }

    @Override
    public MenuPermissionVO currentMenus() {
        Long userId = CurrentUserContext.getUserId();
        String role = CurrentUserContext.get().getRole();
        Set<Long> menuIds = allowedMenuIds(userId, role);
        List<SysMenu> menus = listAllMenus().stream()
                .filter(item -> menuIds.contains(item.getId()))
                .toList();

        MenuPermissionVO vo = new MenuPermissionVO();
        vo.setCodes(menus.stream().map(SysMenu::getMenuCode).toList());
        vo.setMenus(buildTree(menus));
        return vo;
    }

    @Override
    public List<Long> roleMenuIds(String role) {
        checkRole(role);
        return roleMenuMapper.selectList(new LambdaQueryWrapper<SysRoleMenu>()
                        .eq(SysRoleMenu::getRole, role))
                .stream()
                .map(SysRoleMenu::getMenuId)
                .toList();
    }

    @Override
    public List<Long> userMenuIds(Long userId) {
        if (userId == null) {
            throw new BusinessException("请选择用户");
        }
        return userMenuMapper.selectList(new LambdaQueryWrapper<SysUserMenu>()
                        .eq(SysUserMenu::getUserId, userId))
                .stream()
                .map(SysUserMenu::getMenuId)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRole(MenuAssignDTO dto) {
        checkRole(dto.getRole());
        checkMenuIds(dto.getMenuIds());
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRole, dto.getRole()));
        for (Long menuId : distinctIds(dto.getMenuIds())) {
            SysRoleMenu item = new SysRoleMenu();
            item.setRole(dto.getRole());
            item.setMenuId(menuId);
            roleMenuMapper.insert(item);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignUser(MenuAssignDTO dto) {
        if (dto.getUserId() == null) {
            throw new BusinessException("请选择用户");
        }
        checkMenuIds(dto.getMenuIds());
        userMenuMapper.delete(new LambdaQueryWrapper<SysUserMenu>().eq(SysUserMenu::getUserId, dto.getUserId()));
        for (Long menuId : distinctIds(dto.getMenuIds())) {
            SysUserMenu item = new SysUserMenu();
            item.setUserId(dto.getUserId());
            item.setMenuId(menuId);
            userMenuMapper.insert(item);
        }
    }

    @Override
    public boolean canAccessApi(String method, String uri, Long userId, String role) {
        if (matchAny(method, uri, "GET /api/admin/menu/**,PUT /api/admin/menu/**")) {
            return "ADMIN".equals(role);
        }
        Set<Long> menuIds = allowedMenuIds(userId, role);
        return listAllMenus().stream()
                .filter(item -> menuIds.contains(item.getId()))
                .filter(item -> StringUtils.hasText(item.getApiPattern()))
                .anyMatch(item -> matchAny(method, uri, item.getApiPattern()));
    }

    private List<SysMenu> listAllMenus() {
        return menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getStatus, 1)
                .orderByAsc(SysMenu::getSortNo)
                .orderByAsc(SysMenu::getId));
    }

    private Set<Long> allowedMenuIds(Long userId, String role) {
        Set<Long> ids = new LinkedHashSet<>();
        ids.addAll(roleMenuIds(role));
        if (userId != null) {
            ids.addAll(userMenuIds(userId));
        }
        return withParents(ids);
    }

    private Set<Long> withParents(Set<Long> ids) {
        if (ids.isEmpty()) {
            return ids;
        }
        Map<Long, SysMenu> menuMap = listAllMenus().stream().collect(Collectors.toMap(SysMenu::getId, item -> item));
        Set<Long> result = new LinkedHashSet<>(ids);
        for (Long id : new ArrayList<>(ids)) {
            SysMenu menu = menuMap.get(id);
            while (menu != null && menu.getParentId() != null && menu.getParentId() > 0) {
                result.add(menu.getParentId());
                menu = menuMap.get(menu.getParentId());
            }
        }
        return result;
    }

    private List<MenuVO> buildTree(List<SysMenu> menus) {
        Map<Long, List<SysMenu>> childrenMap = menus.stream()
                .collect(Collectors.groupingBy(item -> item.getParentId() == null ? 0L : item.getParentId()));
        return buildChildren(0L, childrenMap);
    }

    private List<MenuVO> buildChildren(Long parentId, Map<Long, List<SysMenu>> childrenMap) {
        return childrenMap.getOrDefault(parentId, List.of()).stream()
                .sorted(Comparator.comparing(SysMenu::getSortNo, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(SysMenu::getId))
                .map(item -> {
                    MenuVO vo = toVO(item);
                    vo.setChildren(buildChildren(item.getId(), childrenMap));
                    return vo;
                })
                .toList();
    }

    private MenuVO toVO(SysMenu menu) {
        MenuVO vo = new MenuVO();
        BeanUtils.copyProperties(menu, vo);
        return vo;
    }

    private void checkRole(String role) {
        if (!ROLE_SET.contains(role)) {
            throw new BusinessException("身份类型不正确");
        }
    }

    private void checkMenuIds(List<Long> menuIds) {
        if (menuIds == null) {
            throw new BusinessException("菜单不能为空");
        }
        if (menuIds.isEmpty()) {
            return;
        }
        Set<Long> oldIds = listAllMenus().stream().map(SysMenu::getId).collect(Collectors.toSet());
        for (Long id : menuIds) {
            if (id == null || !oldIds.contains(id)) {
                throw new BusinessException("菜单数据不正确");
            }
        }
    }

    private List<Long> distinctIds(List<Long> ids) {
        return ids == null ? List.of() : new ArrayList<>(new HashSet<>(ids));
    }

    private boolean matchAny(String method, String uri, String apiPattern) {
        String[] patterns = apiPattern.split(",");
        for (String pattern : patterns) {
            String text = pattern.trim();
            if (!StringUtils.hasText(text)) {
                continue;
            }
            String[] parts = text.split("\\s+", 2);
            if (parts.length == 2 && parts[0].matches("[A-Za-z]+")) {
                if (parts[0].equalsIgnoreCase(method) && pathMatcher.match(parts[1], uri)) {
                    return true;
                }
                continue;
            }
            if (pathMatcher.match(text, uri)) {
                return true;
            }
        }
        return false;
    }
}
