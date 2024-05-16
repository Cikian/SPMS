package com.spms.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QualityTargetDTO {
    private Long qualityTargetId;

    private String targetName;

    private Long qualityAttribute;

    private String targetValue;

    private Integer priority;

    private String metric;

    private String createName;

    private LocalDateTime createTime;
}
