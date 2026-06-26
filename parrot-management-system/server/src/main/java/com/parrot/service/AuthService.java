package com.parrot.service;

import com.parrot.dto.LoginDTO;
import com.parrot.dto.RegisterDTO;
import com.parrot.vo.LoginVO;
import com.parrot.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 登录注册相关业务。
 */
public interface AuthService {

    LoginVO login(LoginDTO dto, HttpServletRequest request);

    UserVO register(RegisterDTO dto);
}
