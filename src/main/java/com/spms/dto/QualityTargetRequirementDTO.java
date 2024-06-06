package com.spms.dto;

import lombok.Data;

@Data
public class QualityTargetRequirementDTO {
    private Long qualityTargetId;
    private Long demandId;
    private String targetName;
    private String demandName;
}
