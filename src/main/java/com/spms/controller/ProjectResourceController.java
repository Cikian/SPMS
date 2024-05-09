package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.ProjectResource;
import com.spms.service.ProjectResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projectResource")
public class ProjectResourceController {

    @Autowired
    private ProjectResourceService projectCostService;

    @GetMapping("getMembersByProId/{proId}")
    public Result getMembersByProId(@PathVariable("proId") int proId) {
        projectCostService.getMembersByProId(proId);
        return null;
    }

   // @PostMapping("/estimateCost")
   // public Result estimateCost(@RequestBody List<ProjectResource> projectCosts){
   //     return projectCostService.estimateCost(projectCosts);
   // }
   //
   // @PostMapping("/actualCost")
   // public Result actualCost(@RequestBody List<ProjectResource> projectCosts){
   //     return projectCostService.actualCost(projectCosts);
   // }
}
