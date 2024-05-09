package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.QualityTarget;
import com.spms.entity.TestPlan;
import com.spms.service.TestPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/testPlan")
public class TestPlanController {

    @Autowired
    private TestPlanService testPlanService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('testPlan:add') ||  hasRole('system_admin')")
    public Result add(@RequestBody TestPlan testPlan) {
        return testPlanService.add(testPlan);
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('testPlan:list') ||  hasRole('system_admin')")
    public Result list(@RequestBody TestPlan testPlan,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestParam(defaultValue = "0") Integer type) {
        return testPlanService.list(testPlan, page, size, type);
    }

}
