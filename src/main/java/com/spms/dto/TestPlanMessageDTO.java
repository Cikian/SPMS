package com.spms.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TestPlanMessageDTO {
    private Long messageId;

    private String content;

    private Long createBy;

    private String createName;

    private String createAvatar;

    private LocalDateTime createTime;
}
