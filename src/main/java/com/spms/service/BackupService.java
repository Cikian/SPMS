package com.spms.service;

import com.spms.dto.Result;

public interface BackupService {

    Result performInitialBackup();

    Result getBackupFileList();
}
