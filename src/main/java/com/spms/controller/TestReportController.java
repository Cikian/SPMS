package com.spms.controller;

import com.spms.service.TestReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testReport")
public class TestReportController {

    @Autowired
    private TestReportService testReportService;
}
