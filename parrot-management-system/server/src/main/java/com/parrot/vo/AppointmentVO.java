package com.parrot.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预约展示对象，后台和客户自己的预约列表都可以复用。
 */
@Data
public class AppointmentVO {

    private Long id;
    private Long customerId;
    private String customerName;
    private Long parrotId;
    private String parrotName;
    private LocalDate appointmentDate;
    private String timeSlot;
    private String phone;
    private String email;
    private String consultType;
    private String content;
    private String status;
    private Long handlerId;
    private String handlerName;
    private LocalDateTime handleTime;
    private String result;
    private String backendRemark;
    private LocalDateTime createTime;
}
