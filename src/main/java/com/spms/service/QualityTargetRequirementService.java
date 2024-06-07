package com.spms.service;

import com.spms.dto.Result;
import com.spms.entity.QualityTargetRequirement;

public interface QualityTargetRequirementService {
    Result byPro(Long proId);

    Result add(QualityTargetRequirement qualityTargetRequirement);

    Result delete(Long demandId, Long targetId);
}
