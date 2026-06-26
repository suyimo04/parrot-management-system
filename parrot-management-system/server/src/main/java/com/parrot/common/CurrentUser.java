package com.parrot.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token 解析后的当前用户信息，只保存接口判断权限需要的几个字段。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUser {

    private Long userId;
    private String username;
    private String role;
}
