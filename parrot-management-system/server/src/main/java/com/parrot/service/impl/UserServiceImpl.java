package com.parrot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parrot.common.BusinessException;
import com.parrot.common.CurrentUserContext;
import com.parrot.common.PageResult;
import com.parrot.common.ResultCode;
import com.parrot.dto.ProfileUpdateDTO;
import com.parrot.dto.UserQueryDTO;
import com.parrot.dto.UserSaveDTO;
import com.parrot.entity.SysUser;
import com.parrot.mapper.SysUserMapper;
import com.parrot.service.UserService;
import com.parrot.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户管理实现，密码相关操作统一在这里加密处理。
 */
@Service
public class UserServiceImpl implements UserService {

    private static final String DEFAULT_PASSWORD = "admin123";

    private final SysUserMapper sysUserMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(SysUserMapper sysUserMapper, BCryptPasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PageResult<UserVO> page(UserQueryDTO query) {
        Page<SysUser> page = new Page<>(safePage(query.getPage()), safeSize(query.getSize()));
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                // 查询条件保持清楚，便于后面答辩说明。
                .like(StringUtils.hasText(query.getUsername()), SysUser::getUsername, query.getUsername())
                .eq(StringUtils.hasText(query.getRole()), SysUser::getRole, query.getRole())
                .eq(query.getStatus() != null, SysUser::getStatus, query.getStatus())
                .orderByDesc(SysUser::getCreateTime);
        Page<SysUser> result = sysUserMapper.selectPage(page, wrapper);
        List<UserVO> records = result.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(records, result.getTotal(), result.getSize(), result.getCurrent());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO add(UserSaveDTO dto) {
        if (!StringUtils.hasText(dto.getPassword())) {
            throw new BusinessException("新增用户时密码不能为空");
        }
        checkRole(dto.getRole());
        checkStatus(dto.getStatus());
        if (findByUsername(dto.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        sysUserMapper.insert(user);
        return toVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO update(Long id, UserSaveDTO dto) {
        SysUser old = getUser(id);
        checkRole(dto.getRole());
        checkStatus(dto.getStatus());
        SysUser sameName = findByUsername(dto.getUsername());
        if (sameName != null && !sameName.getId().equals(id)) {
            throw new BusinessException("用户名已存在");
        }

        old.setUsername(dto.getUsername());
        old.setRealName(dto.getRealName());
        old.setPhone(dto.getPhone());
        old.setEmail(dto.getEmail());
        old.setRole(dto.getRole());
        old.setStatus(dto.getStatus());
        if (StringUtils.hasText(dto.getPassword())) {
            old.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        sysUserMapper.updateById(old);
        return toVO(old);
    }

    @Override
    public void disable(Long id) {
        SysUser user = getUser(id);
        user.setStatus(0);
        sysUserMapper.updateById(user);
    }

    @Override
    public void resetPassword(Long id) {
        SysUser user = getUser(id);
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        sysUserMapper.updateById(user);
    }

    @Override
    public UserVO profile() {
        return toVO(getUser(CurrentUserContext.getUserId()));
    }

    @Override
    public UserVO updateProfile(ProfileUpdateDTO dto) {
        SysUser user = getUser(CurrentUserContext.getUserId());
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        sysUserMapper.updateById(user);
        return toVO(user);
    }

    private SysUser getUser(Long id) {
        if (id == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    private SysUser findByUsername(String username) {
        return sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .last("limit 1"));
    }

    private void checkRole(String role) {
        if (!"ADMIN".equals(role) && !"KEEPER".equals(role) && !"CUSTOMER".equals(role)) {
            throw new BusinessException("角色不正确");
        }
    }

    private void checkStatus(Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态不正确");
        }
    }

    private long safePage(Long page) {
        return page == null || page < 1 ? 1 : page;
    }

    private long safeSize(Long size) {
        return size == null || size < 1 ? 10 : Math.min(size, 100);
    }

    private UserVO toVO(SysUser user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
