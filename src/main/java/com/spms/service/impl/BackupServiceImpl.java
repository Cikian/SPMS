package com.spms.service.impl;

import com.spms.dto.Result;
import com.spms.enums.ResultCode;
import com.spms.service.BackupService;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.spms.constants.SystemConstants.*;
import static io.opentracing.tag.Tags.DB_USER;

@Service
public class BackupServiceImpl implements BackupService {

    private static final String DB_HOST = "140.143.140.103";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "spms";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "chen0809";
    private static final String BACKUP_DIR = "E:/spmsBackup";

    @Override
    public Result performInitialBackup() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String backupFile = BACKUP_DIR + "/initial_backup_" + timestamp + ".sql";

        String command = String.format("mysqldump -h %s -P %s -u %s -p%s %s > %s", DB_HOST, DB_PORT, DB_USER, DB_PASS, DB_NAME, backupFile);

        try {
            Process process = Runtime.getRuntime().exec(new String[]{"cmd", "/c", command});
            int exitCode = process.waitFor();

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), "GBK"));
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                System.err.println("ERROR: " + errorLine);
            }

            if (exitCode == 0) {
                return Result.success("Backup successful");
            } else {
                return Result.fail(ResultCode.FAIL.getCode(), "Backup failed");
            }
        } catch (IOException | InterruptedException e) {
            return Result.fail(ResultCode.FAIL.getCode(), "Backup failed");
        }
    }

}
