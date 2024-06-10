package com.spms;

import com.spms.service.BackupService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.spms.mapper")
@EnableRetry
@EnableTransactionManagement
public class SpmsApplication implements CommandLineRunner {

    @Autowired
    private BackupService backupService;

    public static void main(String[] args) {
        SpringApplication.run(SpmsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        backupService.performInitialBackup();
    }
}
