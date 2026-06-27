package com.parrot.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预约咨询记录，客户提交后由后台确认、完成或驳回。
 */
@Data
@TableName("appointment")
public class Appointment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long customerId;

    private Long parrotId;

    private LocalDate appointmentDate;

    private String timeSlot;

    private String phone;

    private String email;

    private String consultType;

    private String content;

    private String status;

    private Long handlerId;

    private LocalDateTime handleTime;

    private String result;

    private String backendRemark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
