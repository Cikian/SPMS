package com.spms.service;

import com.spms.dto.Result;
import com.spms.entity.TestCase;

public interface TestCaseService {
    Result add(TestCase testCase);

    Result list(Long testPlanId);

    Result queryById(Long testCaseId);

    Result update(TestCase testCase);

    Result delete(Long testCaseId);

    Result calcProTestProgress(Long projectId);
}
