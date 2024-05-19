package com.spms.service;

import com.spms.dto.Result;
import com.spms.entity.TestPlan;

import java.util.List;

public interface TestPlanService {
    Result add(TestPlan testPlan);

    Result list(TestPlan testPlan, Integer page, Integer size, Integer type, Integer status);

    List<TestPlan> listByProId(Long proId);

    Result queryById(Long id);

    Result updateTestPlan(TestPlan testPlan);

    Result queryByDemandId(Long id);
}
