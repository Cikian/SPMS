package com.spms.dto;

import lombok.Data;

@Data
public class BackupDTO {
    private String fileName;
    private String fileSize;
    private String checkCode;
    private String createTime;
    private String desc;
}
