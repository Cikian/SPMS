package com.spms.service.impl;

import com.spms.dto.Result;
import com.spms.service.BackupService;
import org.springframework.boot.system.ApplicationHome;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class BackupServiceImpl implements BackupService {

    private static final String DB_HOST = "140.143.140.103";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "spms";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "chen0809";
//    linux路径
    private static final String DB_INIT_DIR = "/usr/spms/db_backup/init_backup";
    private static final String DB_FULL_DIR = "/usr/spms/db_backup/full_backup";

    ApplicationHome h = new ApplicationHome(getClass());
    File jarF = h.getSource();
    String dirPath = jarF.getParentFile().toString() + "/backup/";

    @Override
    public void performInitialBackup() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        File filePath = new File(dirPath);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        String initBackupDir = dirPath;
        String initBackupFile = initBackupDir + "/initial_backup_" + timestamp + ".sql";

//        linux路径
//        String backupFile = DB_INIT_DIR + "/initial_backup_" + timestamp + ".sql";

        String command = String.format("mysqldump -h %s -P %s -u %s -p%s %s > %s", DB_HOST, DB_PORT, DB_USER, DB_PASS, DB_NAME, initBackupFile);

        Map<String, String> host = getHost();
        try {
            Runtime.getRuntime().exec(new String[]{host.get("shell"), host.get("execParam"), command});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Result getBackupFileList() {
//        linux路径
//        File file = new File(DB_FULL_DIR);
        File file = new File(dirPath);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            String[] fileNames = new String[Objects.requireNonNull(files).length];
            //只保留文件名
            for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
                fileNames[i] = files[i].getName();
            }
            return Result.success(fileNames);
        }
        return Result.success();
    }

    @Override
    public Result restore(String fileName) throws IOException {
        String restoreFile = dirPath + fileName + ".sql";
        String command = String.format("mysql -h %s -P %s -u %s -p%s %s < %s", DB_HOST, DB_PORT, DB_USER, DB_PASS, DB_NAME, restoreFile);

        Map<String, String> host = getHost();
        Runtime.getRuntime().exec(new String[]{host.get("shell"), host.get("execParam"), command});

        return Result.success("恢复成功");
    }

    private static Map<String, String> getHost() {
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
        Map<String, String> result = new HashMap<>();
        result.put("shell", shell);
        result.put("execParam", execParam);
        return result;
    }

}
