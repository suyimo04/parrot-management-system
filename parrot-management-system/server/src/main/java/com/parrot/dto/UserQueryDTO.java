package com.parrot.dto;

import lombok.Data;

/**
 * 用户分页查询条件。
 */
@Data
public class UserQueryDTO {

    private Long page = 1L;

    private Long size = 10L;

    private String username;

    private String role;

    private Integer status;
}
