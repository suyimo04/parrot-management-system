package com.parrot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录表单参数。
 */
@Data
public class LoginDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
