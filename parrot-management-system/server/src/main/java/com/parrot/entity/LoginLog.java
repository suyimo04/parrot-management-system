package com.parrot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志，成功和失败都会记录，便于后台演示安全审计。
 */
@Data
@TableName("login_log")
public class LoginLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String role;

    private String ip;

    private LocalDateTime loginTime;

    private String result;

    private String failReason;
}
