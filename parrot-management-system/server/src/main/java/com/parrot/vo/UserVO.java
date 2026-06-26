package com.parrot.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 对外展示的用户信息，密码字段不能出现在这里。
 */
@Data
public class UserVO {

    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private String role;
    private Integer status;
    private LocalDateTime createTime;
}
