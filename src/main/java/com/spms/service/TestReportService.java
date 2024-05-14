package com.spms.service;

import com.spms.dto.Result;

public interface TestReportService {
    Result list(Long testPlanId);

    Result delete(Long testReportId);

    Result update(Long testReportId, Integer status);
}
