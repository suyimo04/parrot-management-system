package com.parrot.vo;

import lombok.Data;

/**
 * 登录成功后返回给前端的信息，不带密码。
 */
@Data
public class LoginVO {

    private String token;
    private Long userId;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private String role;
}
