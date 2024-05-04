package com.spms.controller;

import com.spms.service.ProjectResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projectResource")
public class ProjectResourceController {

    @Autowired
    private ProjectResourceService projectCostService;

//    @PostMapping("/estimateCost")
//    public Result estimateCost(@RequestBody List<ProjectResource> projectCosts){
//        return projectCostService.estimateCost(projectCosts);
//    }
//
//    @PostMapping("/actualCost")
//    public Result actualCost(@RequestBody List<ProjectResource> projectCosts){
//        return projectCostService.actualCost(projectCosts);
//    }
}
