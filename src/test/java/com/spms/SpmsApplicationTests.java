package com.spms;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Random;

@SpringBootTest
class SpmsApplicationTests {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;
    @SneakyThrows
    @Test
    void testDruid() {
        File file = new File("E:\\IDEA\\Code\\SPMS\\target\\backup\\init");
        File[] files = file.listFiles();
        File file1 = files[0];
        System.out.println(file1);
        System.out.println(file1.getName());

    }

}
