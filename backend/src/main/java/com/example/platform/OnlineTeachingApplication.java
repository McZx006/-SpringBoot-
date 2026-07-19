package com.example.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.example.platform.mapper")
public class OnlineTeachingApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnlineTeachingApplication.class, args);
    }
}
