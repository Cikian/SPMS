package com.spms.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TestPlanDTO {
    private Long testPlanId;
    private String planName;
    private Long projectId;
    private String projectName;
    private Long head;
    private String headName;
    private String headAvatar;
    private String demandName;
    private Integer progress;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String creatorName;
    private String creatorAvatar;
    private LocalDateTime createTime;
    private Integer reviewStatus;
}
