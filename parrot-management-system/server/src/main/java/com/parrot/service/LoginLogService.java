package com.parrot.service;

import com.parrot.common.PageResult;
import com.parrot.dto.LoginLogQueryDTO;
import com.parrot.entity.LoginLog;

/**
 * 登录日志查询业务。
 */
public interface LoginLogService {

    PageResult<LoginLog> page(LoginLogQueryDTO query);
}
