package com.spms.controller;

import com.spms.dto.RatedTimeCostDTO;
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
    @PreAuthorize("hasAuthority('ratedTimeCost:update') || hasRole('system_admin')")
    public Result updateCost(@RequestBody RatedTimeCost ratedTimeCost) {
        return ratedTimeCostService.updateCost(ratedTimeCost);
    }

    @PostMapping("/list")
    public Result list(@RequestBody RatedTimeCostDTO ratedTimeCost,
                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return ratedTimeCostService.list(ratedTimeCost, page, size);
    }

    @GetMapping("/queryById/{ratedTimeCostId}")
    public Result queryById(@PathVariable("ratedTimeCostId") Long ratedTimeCostId) {
        return ratedTimeCostService.queryById(ratedTimeCostId);
    }
}
