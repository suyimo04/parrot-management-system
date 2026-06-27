package com.parrot.dto;

import lombok.Data;

/**
 * 后台处理预约时填写处理结果或备注。
 */
@Data
public class AppointmentHandleDTO {

    private String result;

    private String backendRemark;
}
