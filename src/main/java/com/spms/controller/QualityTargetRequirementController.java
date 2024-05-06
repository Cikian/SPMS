package com.spms.controller;

import com.spms.service.QualityTargetRequirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qualityTargetRequirement")
public class QualityTargetRequirementController {

    @Autowired
    private QualityTargetRequirementService qualityTargetRequirementService;
}
