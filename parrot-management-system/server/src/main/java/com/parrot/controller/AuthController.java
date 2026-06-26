package com.parrot.controller;

import com.parrot.common.ApiResult;
import com.parrot.dto.LoginDTO;
import com.parrot.dto.RegisterDTO;
import com.parrot.service.AuthService;
import com.parrot.vo.LoginVO;
import com.parrot.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录注册接口，前后端联调时最先用到。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResult<LoginVO> login(@Valid @RequestBody LoginDTO dto, HttpServletRequest request) {
        return ApiResult.success(authService.login(dto, request));
    }

    @PostMapping("/register")
    public ApiResult<UserVO> register(@Valid @RequestBody RegisterDTO dto) {
        return ApiResult.success("注册成功", authService.register(dto));
    }
}
