package com.parrot.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.parrot.entity.SysUser;
import com.parrot.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 初始化演示账号，方便项目演示和答辩，正式环境可以按需要关闭。
 */
@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper sysUserMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(SysUserMapper sysUserMapper, BCryptPasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        try {
            Long count = sysUserMapper.selectCount(new LambdaQueryWrapper<>());
            if (count != null && count > 0) {
                return;
            }

            addUser("admin", "管理员", "ADMIN");
            addUser("keeper", "饲养员", "KEEPER");
            addUser("customer", "演示客户", "CUSTOMER");
            log.info("默认演示账号初始化完成：admin/keeper/customer，密码均为 admin123");
        } catch (Exception e) {
            // 有些同学会先跑后端再建库，这里不让服务直接退出，建好库后重启即可初始化账号。
            log.warn("数据库暂时不可用，默认账号本次未初始化：{}", e.getMessage());
        }
    }

    private void addUser(String username, String realName, String role) {
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("admin123"));
        user.setRealName(realName);
        user.setRole(role);
        user.setStatus(1);
        sysUserMapper.insert(user);
    }
}
