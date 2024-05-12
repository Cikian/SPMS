package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.TestCase;
import com.spms.service.TestCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/testCase")
public class TestCaseController {

    @Autowired
    private TestCaseService testCaseService;

    @PostMapping("/add")
    public Result add(@RequestBody TestCase testCase) {
        return testCaseService.add(testCase);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TestCase testCase) {
        return testCaseService.update(testCase);
    }

    @GetMapping("/list/{testPlanId}")
    public Result list(@PathVariable("testPlanId") Long testPlanId) {
        return testCaseService.list(testPlanId);
    }

    @GetMapping("/queryById/{testCaseId}")
    public Result queryById(@PathVariable("testCaseId") Long testCaseId) {
        return testCaseService.queryById(testCaseId);
    }

    @PostMapping("/delete/{testCaseId}")
    public Result delete(@PathVariable("testCaseId") Long testCaseId) {
        return testCaseService.delete(testCaseId);
    }
}
