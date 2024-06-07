package com.spms.entity;

import lombok.Data;

@Data
public class QualityTargetRequirement {
    private Long qualityTargetId;
    private Long demandId;
    private Boolean delFlag;
}
