package com.spms.service;

import com.spms.dto.Result;
import com.spms.dto.TestPlanDTO;
import com.spms.entity.TestPlan;

import java.util.List;

public interface TestPlanService {
    Result add(TestPlan testPlan);

    Result list(TestPlan testPlan, Integer page, Integer size, Integer type, Integer status);

    List<TestPlanDTO> listByProId(Long proId, String testPlanName, Integer status);

    Result queryById(Long id);

    Result updateTestPlan(TestPlan testPlan);

    Result queryByDemandId(Long id);

    Result updateReviewStatus(Long testPlanId, Integer reviewResult);

    Result listAllPendingByProId(Long proId);

    Result listMySubmit(TestPlan testPlan, Integer page, Integer size, Integer reviewStatus);

    Result finishTestPlan(Long testPlanId);
}
