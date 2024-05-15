package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.TestPlanMessage;
import com.spms.service.TestPlanMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/testPlanMessage")
public class TestPlanMessageController {

    @Autowired
    private TestPlanMessageService testPlanMessageService;

    @PostMapping("/add")
    public Result add(@RequestBody TestPlanMessage testPlanMessage) {
        return testPlanMessageService.add(testPlanMessage);
    }

    @GetMapping("/list/{testPlanId}")
    public Result list(@PathVariable("testPlanId") Long testPlanId) {
        return testPlanMessageService.list(testPlanId);
    }

}
