package com.spms.controller;

import com.spms.service.RequirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/requirement")
public class RequirementController {

    @Autowired
    private RequirementService requirementService;
}
