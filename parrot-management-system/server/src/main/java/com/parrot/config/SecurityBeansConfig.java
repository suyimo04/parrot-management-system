package com.parrot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 这里只使用 BCrypt 做密码加密，不引入完整 Spring Security 登录流程。
 */
@Configuration
public class SecurityBeansConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
