package com.parrot.controller;

import com.parrot.common.ApiResult;
import com.parrot.common.PageResult;
import com.parrot.dto.ProfileUpdateDTO;
import com.parrot.dto.UserQueryDTO;
import com.parrot.dto.UserSaveDTO;
import com.parrot.service.UserService;
import com.parrot.vo.UserVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理接口，后台管理员使用。
 */
@RestController
@RequestMapping("/api/admin/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/page")
    public ApiResult<PageResult<UserVO>> page(UserQueryDTO query) {
        return ApiResult.success(userService.page(query));
    }

    @PostMapping
    public ApiResult<UserVO> add(@Valid @RequestBody UserSaveDTO dto) {
        return ApiResult.success("新增用户成功", userService.add(dto));
    }

    @PutMapping("/{id}")
    public ApiResult<UserVO> update(@PathVariable Long id, @Valid @RequestBody UserSaveDTO dto) {
        return ApiResult.success("修改用户成功", userService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> disable(@PathVariable Long id) {
        userService.disable(id);
        return ApiResult.success("用户已禁用", null);
    }

    @PutMapping("/{id}/reset-password")
    public ApiResult<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return ApiResult.success("密码已重置为 admin123", null);
    }
}
