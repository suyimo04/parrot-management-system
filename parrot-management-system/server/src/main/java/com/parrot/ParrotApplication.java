package com.parrot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 后端启动入口，先把基础能力跑通，后续模块都挂在这个工程下面。
 */
@SpringBootApplication
public class ParrotApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParrotApplication.class, args);
    }
}
