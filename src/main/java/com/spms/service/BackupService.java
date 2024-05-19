package com.spms.service;

import com.spms.dto.BackupDTO;
import com.spms.dto.Result;

import java.io.IOException;

public interface BackupService {

    void performInitialBackup();

    Result getBackupFileList();

    Result restore(BackupDTO backupDTO);

    Result restoreInit();
}
