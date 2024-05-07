package com.spms.service;

import com.spms.dto.Result;
import com.spms.entity.QualityTarget;

public interface QualityTargetService {
    Result add(QualityTarget qualityTarget);

    Result delete(Long[] ids);

    Result list(QualityTarget qualityTarget, Integer page, Integer size);
}
