package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.Demand;
import com.spms.enums.ErrorCode;
import com.spms.service.DemandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/demand")
public class DemandController {

    @Autowired
    private DemandService demandService;

    @GetMapping("/list/{proId}")
    public Result getDemandByProId(@PathVariable("proId") Long proId) {
        Map<String, List<Demand>> demands = demandService.getAllDemandsByProId(proId);
        Integer code = demands.isEmpty() ? ErrorCode.GET_FAIL : ErrorCode.GET_SUCCESS;
        String msg = demands.isEmpty() ? "无数据" : "获取成功";
        return new Result(code, msg, demands);
    }

    @PostMapping
    public Result addDemand(@RequestBody Demand demand) {
        System.out.println("新需求：" + demand);
        Boolean b = demandService.addDemand(demand);
        Integer code = b ? ErrorCode.ADD_SUCCESS : ErrorCode.ADD_FAIL;
        String msg = b ? "添加成功" : "添加失败";
        return new Result(code, msg, null);
    }

    @PutMapping("/changeStatus/{demandId}/{status}")
    public Result changeStatus(@PathVariable("demandId") Long demandId, @PathVariable("status") Integer status) {
        Boolean b = demandService.changeStatus(demandId, status);
        Integer code = b ? ErrorCode.UPDATE_SUCCESS : ErrorCode.UPDATE_FAIL;
        String msg = b ? "更新成功" : "更新失败";
        return new Result(code, msg, null);
    }

    @PutMapping("/changeHeadId/{demandId}/{headId}")
    public Result changeHeadId(@PathVariable("demandId") Long demandId, @PathVariable("headId") Long headId) {
        Boolean b = demandService.changeHeadId(demandId, headId);
        Integer code = b ? ErrorCode.UPDATE_SUCCESS : ErrorCode.UPDATE_FAIL;
        String msg = b ? "更新成功" : "更新失败";
        return new Result(code, msg, null);
    }

    @PutMapping("/changePriority/{demandId}/{priority}")
    public Result changePriority(@PathVariable("demandId") Long demandId, @PathVariable("priority") Integer priority) {
        Boolean b = demandService.changePriority(demandId, priority);
        Integer code = b ? ErrorCode.UPDATE_SUCCESS : ErrorCode.UPDATE_FAIL;
        String msg = b ? "更新成功" : "更新失败";
        return new Result(code, msg, null);
    }

    @PutMapping("/changeType/{demandId}/{type}")
    public Result changeType(@PathVariable("demandId") Long demandId, @PathVariable("type") Long type) {
        Boolean b = demandService.changeType(demandId, type);
        Integer code = b ? ErrorCode.UPDATE_SUCCESS : ErrorCode.UPDATE_FAIL;
        String msg = b ? "更新成功" : "更新失败";
        return new Result(code, msg, null);
    }

    @PutMapping("/changeSource/{demandId}/{source}")
    public Result changeSource(@PathVariable("demandId") Long demandId, @PathVariable("source") Long source) {
        Boolean b = demandService.changeSource(demandId, source);
        Integer code = b ? ErrorCode.UPDATE_SUCCESS : ErrorCode.UPDATE_FAIL;
        String msg = b ? "更新成功" : "更新失败";
        return new Result(code, msg, null);
    }


    @PutMapping("/changeDesc")
    public Result changeDesc(@RequestBody Demand demand) {
        System.out.println("需求描述：" + demand.getDemandDesc());
        Boolean b = demandService.changeDesc(demand.getDemandId(), demand.getDemandDesc());
        Integer code = b ? ErrorCode.UPDATE_SUCCESS : ErrorCode.UPDATE_FAIL;
        String msg = b ? "更新成功" : "更新失败";
        return new Result(code, msg, null);
    }

    @GetMapping("/child/{demandId}")
    public Result getChildDemands(@PathVariable("demandId") Long demandId) {
        List<Demand> demands = demandService.getChildDemands(demandId);
        Integer code = demands.isEmpty() ? ErrorCode.GET_FAIL : ErrorCode.GET_SUCCESS;
        String msg = demands.isEmpty() ? "无数据" : "获取成功";
        return new Result(code, msg, demands);
    }
}
