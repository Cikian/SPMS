package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.TestReport;
import com.spms.service.TestReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/testReport")
public class TestReportController {

    @Autowired
    private TestReportService testReportService;

    @GetMapping("/list/{testPlanId}")
    public Result list(@PathVariable("testPlanId") Long testPlanId) {
        return testReportService.list(testPlanId);
    }

    @PostMapping("/update/{testReportId}/{status}")
    @PreAuthorize("hasAuthority('testReport:update') ||  hasRole('system_admin')")
    public Result update(@PathVariable("testReportId") Long testReportId, @PathVariable("status") Integer status) {
        return testReportService.update(testReportId, status);
    }

    @PostMapping("/delete/{testReportId}")
    @PreAuthorize("hasAuthority('testReport:delete') ||  hasRole('system_admin')")
    public Result delete(@PathVariable("testReportId") Long testReportId) {
        return testReportService.delete(testReportId);
    }
}
