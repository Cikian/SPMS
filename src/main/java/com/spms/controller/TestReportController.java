package com.spms.controller;

import com.spms.dto.Result;
import com.spms.service.TestReportService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/delete/{testReportId}")
    public Result delete(@PathVariable("testReportId") Long testReportId) {
        return testReportService.delete(testReportId);
    }
}
