package com.spms.controller;

import com.spms.dto.Result;
import com.spms.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/backup")
public class BackupController {

    @Autowired
    private BackupService backupService;

    @GetMapping("/getBackupFileList")
    @PreAuthorize("hasRole('system_admin')")
    public Result getBackupFileList(){
        return backupService.getBackupFileList();
    }

    //恢复备份
    @PostMapping("/restore")
    @PreAuthorize("hasRole('system_admin')")
    public Result restore(@RequestParam("fileName") String fileName) throws IOException {
        return backupService.restore(fileName);
    }
}
