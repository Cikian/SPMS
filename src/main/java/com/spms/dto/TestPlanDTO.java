package com.spms.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TestPlanDTO {
    private Long testPlanId;
    private Long projectId;
    private Long head;
    private String planName;
    private String projectName;
    private String demandName;
    private String headName;
    private Integer progress;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
