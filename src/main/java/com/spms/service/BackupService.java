package com.spms.service;

import com.spms.dto.BackupDTO;
import com.spms.dto.Result;

public interface BackupService {

    void performInitialBackup();

    Result getBackupFileList();

    Result restore(BackupDTO backupDTO);

    Result restoreInit();
}
