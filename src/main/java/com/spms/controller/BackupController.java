package com.spms.controller;

import com.spms.dto.BackupDTO;
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
        return backupService.getFullBackupFileList();
    }

    @PostMapping("/restore")
    @PreAuthorize("hasRole('system_admin')")
    public Result restore(@RequestBody BackupDTO backupDTO) {
        return backupService.restore(backupDTO);
    }

    @GetMapping("/restoreInit")
    @PreAuthorize("hasRole('system_admin')")
    public Result restoreInit() {
        return backupService.restoreInit();
    }
}
