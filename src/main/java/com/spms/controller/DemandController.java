package com.spms.controller;

import com.spms.dto.Result;
import com.spms.service.DemandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demand")
public class DemandController {

    @Autowired
    private DemandService demandService;

    @GetMapping("/list/{proId}")
    public Result getDemandByProId(@PathVariable("proId") Long proId){
        return demandService.getAllDemandsByProId(proId);
    }
}
