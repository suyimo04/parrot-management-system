package com.parrot.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

/**
 * 客户提交预约时填写的内容。
 */
@Data
public class AppointmentSubmitDTO {

    @NotNull(message = "预约鹦鹉不能为空")
    private Long parrotId;

    @NotNull(message = "预约日期不能为空")
    private LocalDate appointmentDate;

    @NotBlank(message = "预约时间段不能为空")
    private String timeSlot;

    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String consultType;

    private String content;
}
