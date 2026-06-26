package com.parrot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.parrot.common.BusinessException;
import com.parrot.common.CurrentUser;
import com.parrot.common.IpUtils;
import com.parrot.common.JwtUtils;
import com.parrot.common.ResultCode;
import com.parrot.dto.LoginDTO;
import com.parrot.dto.RegisterDTO;
import com.parrot.entity.LoginLog;
import com.parrot.entity.SysUser;
import com.parrot.mapper.LoginLogMapper;
import com.parrot.mapper.SysUserMapper;
import com.parrot.service.AuthService;
import com.parrot.vo.LoginVO;
import com.parrot.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 认证业务实现，先保证毕业设计里登录、注册、日志这条线稳定。
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final LoginLogMapper loginLogMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthServiceImpl(SysUserMapper sysUserMapper,
                           LoginLogMapper loginLogMapper,
                           BCryptPasswordEncoder passwordEncoder,
                           JwtUtils jwtUtils) {
        this.sysUserMapper = sysUserMapper;
        this.loginLogMapper = loginLogMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public LoginVO login(LoginDTO dto, HttpServletRequest request) {
        SysUser user = findByUsername(dto.getUsername());
        String ip = IpUtils.getIp(request);

        if (user == null) {
            saveLoginLog(dto.getUsername(), null, ip, "失败", "用户不存在");
            throw new BusinessException(ResultCode.BAD_REQUEST, "用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() == 0) {
            saveLoginLog(dto.getUsername(), user.getRole(), ip, "失败", "账号已被禁用");
            throw new BusinessException(ResultCode.FORBIDDEN, "账号已被禁用");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            saveLoginLog(dto.getUsername(), user.getRole(), ip, "失败", "密码错误");
            throw new BusinessException(ResultCode.BAD_REQUEST, "用户名或密码错误");
        }

        String token = jwtUtils.createToken(new CurrentUser(user.getId(), user.getUsername(), user.getRole()));
        saveLoginLog(user.getUsername(), user.getRole(), ip, "成功", null);
        log.info("用户 {} 登录成功", user.getUsername());

        LoginVO vo = toLoginVO(user);
        vo.setToken(token);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO register(RegisterDTO dto) {
        if (findByUsername(dto.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(StringUtils.hasText(dto.getRealName()) ? dto.getRealName() : dto.getUsername());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setRole("CUSTOMER");
        user.setStatus(1);
        sysUserMapper.insert(user);
        return toUserVO(user);
    }

    private SysUser findByUsername(String username) {
        return sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .last("limit 1"));
    }

    private void saveLoginLog(String username, String role, String ip, String result, String failReason) {
        LoginLog log = new LoginLog();
        log.setUsername(username);
        log.setRole(role);
        log.setIp(ip);
        log.setLoginTime(LocalDateTime.now());
        log.setResult(result);
        log.setFailReason(failReason);
        loginLogMapper.insert(log);
    }

    private LoginVO toLoginVO(SysUser user) {
        LoginVO vo = new LoginVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setRole(user.getRole());
        return vo;
    }

    private UserVO toUserVO(SysUser user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}
