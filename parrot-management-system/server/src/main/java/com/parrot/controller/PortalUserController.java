package com.parrot.controller;

import com.parrot.common.ApiResult;
import com.parrot.dto.ProfileUpdateDTO;
import com.parrot.service.UserService;
import com.parrot.vo.UserVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台客户个人中心接口。
 */
@RestController
@RequestMapping("/api/portal/user")
public class PortalUserController {

    private final UserService userService;

    public PortalUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ApiResult<UserVO> profile() {
        return ApiResult.success(userService.profile());
    }

    @PutMapping("/profile")
    public ApiResult<UserVO> updateProfile(@Valid @RequestBody ProfileUpdateDTO dto) {
        return ApiResult.success("个人资料已保存", userService.updateProfile(dto));
    }
}
