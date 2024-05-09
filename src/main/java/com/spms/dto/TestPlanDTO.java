package com.spms.dto;

import lombok.Data;

@Data
public class TestPlanDTO {
    private Long testPlanId;
    private String planName;
    private Integer progress;
    private String headName;
    private String startTime;
    private String endTime;
}
