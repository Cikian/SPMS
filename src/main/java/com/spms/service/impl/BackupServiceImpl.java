package com.spms.service.impl;

import com.spms.dto.Result;
import com.spms.enums.ResultCode;
import com.spms.service.BackupService;
import org.springframework.boot.system.ApplicationHome;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class BackupServiceImpl implements BackupService {

    private static final String DB_HOST = "140.143.140.103";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "spms";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "chen0809";
    private static final String DB_INIT_DIR = "/usr/spms/db_backup/init_backup";

    ApplicationHome h = new ApplicationHome(getClass());
    File jarF = h.getSource();
    String dirPath = jarF.getParentFile().toString() + "/backup/";

    @Override
    public Result performInitialBackup() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());


        File filePath = new File(dirPath);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        String backupDir = dirPath;
        String backupFile = backupDir + "/initial_backup_" + timestamp + ".sql";

//        String backupFile = DB_INIT_DIR + "/initial_backup_" + timestamp + ".sql";

        String command = String.format("mysqldump -h %s -P %s -u %s -p%s %s > %s", DB_HOST, DB_PORT, DB_USER, DB_PASS, DB_NAME, backupFile);

        String property = System.getProperty("os.name");
        String execParam = "";
        String shell = "";
        if (property.toLowerCase().contains("windows")) {
            shell = "cmd";
            execParam = "/c";
        } else {
            shell = "sh";
            execParam = "-c";
        }

        try {
            Process process = Runtime.getRuntime().exec(new String[]{shell, execParam, command});
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                return Result.success("备份成功");
            } else {
                return Result.fail(ResultCode.FAIL.getCode(), "备份失败");
            }
        } catch (IOException | InterruptedException e) {
            return Result.fail(ResultCode.FAIL.getCode(), "备份失败");
        }
    }

    @Override
    public Result getBackupFileList() {
        return null;
    }
}
