package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.QualityTarget;
import com.spms.service.QualityTargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/qualityTarget")
public class QualityTargetController {

    @Autowired
    private QualityTargetService qualityTargetService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('qualityTarget:add') || hasRole('system_admin')")
    public Result add(@RequestBody QualityTarget qualityTarget) {
        return qualityTargetService.add(qualityTarget);
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('qualityTarget:delete') || hasRole('system_admin')")
    public Result delete(@RequestBody Long[] ids) {
        return qualityTargetService.delete(ids);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('qualityTarget:update') || hasRole('system_admin')")
    public Result update(@RequestBody QualityTarget qualityTarget) {
        return qualityTargetService.update(qualityTarget);
    }

    @PostMapping("/list")
    public Result list(@RequestBody QualityTarget qualityTarget,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer size) {
        return qualityTargetService.list(qualityTarget, page, size);
    }

    @GetMapping("/queryById/{id}")
    public Result queryById(@PathVariable("id") Long id) {
        return qualityTargetService.queryById(id);
    }
}
