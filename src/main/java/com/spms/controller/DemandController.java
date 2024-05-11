package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.Demand;
import com.spms.enums.ErrorCode;
import com.spms.service.DemandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/demand")
public class DemandController {

    @Autowired
    private DemandService demandService;

    @GetMapping("/list/{proId}")
    public Result getDemandByProId(@PathVariable("proId") Long proId){
        return demandService.getAllDemandsByProId(proId);
    }

    @PostMapping
    public Result addDemand(@RequestBody Demand demand){
        System.out.println("新需求：" + demand);
        Boolean b = demandService.addDemand(demand);
        Integer code = b ? ErrorCode.ADD_SUCCESS : ErrorCode.ADD_FAIL;
        String msg = b ? "添加成功" : "添加失败";
        return new Result(code, msg, null);
    }
}
