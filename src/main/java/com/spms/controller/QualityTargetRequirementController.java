package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.QualityTarget;
import com.spms.entity.QualityTargetRequirement;
import com.spms.service.QualityTargetRequirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/qualityTargetRequirement")
public class QualityTargetRequirementController {

    @Autowired
    private QualityTargetRequirementService qualityTargetRequirementService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('qualityTargetRequirement:add') || hasRole('system_admin')")
    public Result add(@RequestBody QualityTargetRequirement qualityTargetRequirement) {
        return qualityTargetRequirementService.add(qualityTargetRequirement);
    }

    @PostMapping("/delete/{demandId}/{targetId}")
    @PreAuthorize("hasAuthority('qualityTargetRequirement:delete') || hasRole('system_admin')")
    public Result delete(@PathVariable("demandId") Long demandId, @PathVariable("targetId") Long targetId) {
        return qualityTargetRequirementService.delete(demandId, targetId);
    }

    @GetMapping("/byPro/{proId}")
    public Result byPro(@PathVariable("proId") Long proId) {
        return qualityTargetRequirementService.byPro(proId);
    }
}
