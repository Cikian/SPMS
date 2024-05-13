package com.spms.service;

import com.spms.dto.Result;
import com.spms.entity.TestPlan;

public interface TestPlanService {
    Result add(TestPlan testPlan);

    Result list(TestPlan testPlan, Integer page, Integer size, Integer type, Integer status);

    Result queryById(Long id);

    Result updateTestPlan(TestPlan testPlan);
}
