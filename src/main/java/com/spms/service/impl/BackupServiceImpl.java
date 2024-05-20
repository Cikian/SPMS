package com.spms.service.impl;

import com.spms.dto.BackupDTO;
import com.spms.dto.Result;
import com.spms.enums.ResultCode;
import com.spms.service.BackupService;
import com.spms.utils.FileCheckCodeUtil;
import org.springframework.boot.system.ApplicationHome;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

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

        File filePath = new File(dirPath + "full");
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        String initBackupDir = dirPath + "full";
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

        File file = new File(dirPath + "full");
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null || files.length == 0) {
                return Result.success("暂无数据");
            }

            List<BackupDTO> backupDTOList = Arrays.stream(files).map(f -> {
                BackupDTO backupDTO = new BackupDTO();
                try {
                    String checksum = FileCheckCodeUtil.generateChecksum(f.getAbsolutePath());
                    backupDTO.setCheckCode(checksum);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                backupDTO.setFileName(f.getName().substring(0, f.getName().lastIndexOf(".")));
                backupDTO.setFileSize(String.valueOf(f.length()));
                backupDTO.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(f.lastModified())));
                backupDTO.setDesc("每日全量备份");
                return backupDTO;
            }).sorted(Comparator.comparing(BackupDTO::getCreateTime).reversed()).toList();
            return Result.success(backupDTOList);
        }
        return Result.success();
    }

    @Override
    public Result restore(BackupDTO backupDTO) {
        String restoreFile = dirPath + "full/" + backupDTO.getFileName() + ".sql";
        try {
            String currentChecksum = FileCheckCodeUtil.generateChecksum(restoreFile);
            if (!currentChecksum.equals(backupDTO.getCheckCode())) {
                return Result.fail(ResultCode.FAIL.getCode(), "校验码不匹配，文件可能已损坏或被篡改");
            }

            String command = String.format("mysql -h %s -P %s -u %s -p%s %s < %s", DB_HOST, DB_PORT, DB_USER, DB_PASS, DB_NAME, restoreFile);
            Map<String, String> host = getHost();
            Runtime.getRuntime().exec(new String[]{host.get("shell"), host.get("execParam"), command});

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Result.success("恢复成功");
    }

    @Override
    public Result restoreInit() {
        try {
            String command = String.format("mysql -h %s -P %s -u %s -p%s %s < %s", DB_HOST, DB_PORT, DB_USER, DB_PASS, DB_NAME, dirPath + "init/initial_backup_20240519091341.sql");
            Map<String, String> host = getHost();
            Runtime.getRuntime().exec(new String[]{host.get("shell"), host.get("execParam"), command});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
