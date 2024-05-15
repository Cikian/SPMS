package com.spms.service;

import com.spms.dto.Result;
import com.spms.entity.TestPlanMessage;

public interface TestPlanMessageService {
    Result add(TestPlanMessage testPlanMessage);

    Result list(Long testPlanId);
}
