package com.parrot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parrot.common.PageResult;
import com.parrot.dto.LoginLogQueryDTO;
import com.parrot.entity.LoginLog;
import com.parrot.mapper.LoginLogMapper;
import com.parrot.service.LoginLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalTime;

/**
 * 登录日志查询实现，主要给管理员查看登录情况。
 */
@Service
public class LoginLogServiceImpl implements LoginLogService {

    private final LoginLogMapper loginLogMapper;

    public LoginLogServiceImpl(LoginLogMapper loginLogMapper) {
        this.loginLogMapper = loginLogMapper;
    }

    @Override
    public PageResult<LoginLog> page(LoginLogQueryDTO query) {
        Page<LoginLog> page = new Page<>(safePage(query.getPage()), safeSize(query.getSize()));
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<LoginLog>()
                .like(StringUtils.hasText(query.getUsername()), LoginLog::getUsername, query.getUsername())
                .eq(StringUtils.hasText(query.getResult()), LoginLog::getResult, query.getResult())
                .ge(query.getStartDate() != null, LoginLog::getLoginTime,
                        query.getStartDate() == null ? null : query.getStartDate().atStartOfDay())
                .le(query.getEndDate() != null, LoginLog::getLoginTime,
                        query.getEndDate() == null ? null : query.getEndDate().atTime(LocalTime.MAX))
                .orderByDesc(LoginLog::getLoginTime);
        return PageResult.fromPage(loginLogMapper.selectPage(page, wrapper));
    }

    private long safePage(Long page) {
        return page == null || page < 1 ? 1 : page;
    }

    private long safeSize(Long size) {
        return size == null || size < 1 ? 10 : Math.min(size, 100);
    }
}
