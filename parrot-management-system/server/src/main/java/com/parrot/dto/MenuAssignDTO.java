package com.parrot.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单授权提交参数，角色和用户共用这一份结构。
 */
@Data
public class MenuAssignDTO {

    private String role;

    private Long userId;

    @NotNull(message = "请选择菜单")
    private List<Long> menuIds = new ArrayList<>();
}
