package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.ProjectResource;
import com.spms.service.ProjectResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
