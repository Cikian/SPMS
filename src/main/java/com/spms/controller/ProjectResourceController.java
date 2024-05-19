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

    @GetMapping("/getMembersByProId")
    public Result getMembersByProId(@RequestParam("proId") Long proId, @RequestParam("userName") String userName) {
        return projectCostService.getMembersByProId(proId,userName);
    }
}
