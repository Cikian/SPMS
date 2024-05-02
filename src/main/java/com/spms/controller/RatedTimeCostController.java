package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.RatedTimeCost;
import com.spms.service.RatedTimeCostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ratedTimeCost")
public class RatedTimeCostController {

    @Autowired
    private RatedTimeCostService ratedTimeCostService;

    @PostMapping("/updateCost")
    @PreAuthorize("hasRole('system_admin')")
    public Result updateCost(@RequestBody RatedTimeCost ratedTimeCost) {
        return ratedTimeCostService.updateCost(ratedTimeCost);
    }

    @PostMapping("/list")
    @PreAuthorize("hasRole('system_admin')")
    public Result list(@RequestBody RatedTimeCost ratedTimeCost,
                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return ratedTimeCostService.list(ratedTimeCost, page, size);
    }

    @GetMapping("/queryById/{ratedTimeCostId}")
    @PreAuthorize("hasRole('system_admin')")
    public Result queryById(@PathVariable("ratedTimeCostId") Long ratedTimeCostId) {
        return ratedTimeCostService.queryById(ratedTimeCostId);
    }
}