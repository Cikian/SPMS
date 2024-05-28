package com.spms.service;

import com.spms.dto.BackupDTO;
import com.spms.dto.Result;

import java.io.IOException;

public interface BackupService {

    void performInitialBackup() throws IOException;

    Result getFullBackupFileList();

    Result restore(BackupDTO backupDTO);

    Result restoreInit();
}
