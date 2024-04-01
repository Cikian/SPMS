package com.spms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@MapperScan("com.spms.mapper")
@EnableRetry
public class SpmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpmsApplication.class, args);
    }

}
