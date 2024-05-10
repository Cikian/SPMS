package com.spms.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TestPlanDTO {
    private Long testPlanId;
    private String planName;
    private Integer progress;
    private String headName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
