package com.parrot.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图表里的通用名称和值。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartItemVO {

    private String name;
    private Long value;
}
