package com.parrot.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 客户注册参数，角色固定由后端写死为 CUSTOMER。
 */
@Data
public class RegisterDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名不能超过50个字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 30, message = "密码长度应为6到30位")
    private String password;

    private String realName;

    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;
}
