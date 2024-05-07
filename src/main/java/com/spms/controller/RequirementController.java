package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.Requirement;
import com.spms.service.RequirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/requirement")
public class RequirementController {

    @Autowired
    private RequirementService requirementService;

    @PostMapping("/add")
    public Result addRequirement(@RequestBody Requirement requirement) {
        return requirementService.add(requirement);
    }
}
