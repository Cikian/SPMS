package com.spms.controller;

import com.spms.dto.Result;
import com.spms.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backup")
public class BackupController {

    @Autowired
    private BackupService backupService;

    @PostMapping("/initial")
    public Result performInitialBackup() {
        return backupService.performInitialBackup();
    }
}
