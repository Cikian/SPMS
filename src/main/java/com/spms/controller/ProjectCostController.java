package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.ProjectCost;
import com.spms.service.ProjectCostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/projectCost")
public class ProjectCostController {

    @Autowired
    private ProjectCostService projectCostService;

    @PostMapping("/estimateCost")
    public Result estimateCost(@RequestBody List<ProjectCost> projectCosts){
        return projectCostService.estimateCost(projectCosts);
    }
}
