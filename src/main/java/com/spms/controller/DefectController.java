package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.Defect;
import com.spms.entity.Demand;
import com.spms.enums.ErrorCode;
import com.spms.service.DefectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/defect")
public class DefectController {

    @Autowired
    private DefectService defectService;

    @GetMapping("/list/{proId}")
    public Result getDemandByProId(@PathVariable("proId") Long proId) {
        Map<String, List<Defect>> demands = defectService.getAllDefectsByProId(proId);
        Integer code = demands.isEmpty() ? ErrorCode.GET_FAIL : ErrorCode.GET_SUCCESS;
        String msg = demands.isEmpty() ? "无数据" : "获取成功";
        return new Result(code, msg, demands);
    }

    @GetMapping("/{demandId}")
    public Result getDemandById(@PathVariable("demandId") Long demandId) {
        Defect demand = defectService.getDefectById(demandId);
        Integer code = demand == null ? ErrorCode.GET_FAIL : ErrorCode.GET_SUCCESS;
        String msg = demand == null ? "无数据" : "获取成功";
        return new Result(code, msg, demand);
    }

    @GetMapping("/byHead/{proId}")
    public Result getDemandByHeadId(@PathVariable("proId") Long proId) {
        List<Defect> allDefectsByHeaderId = defectService.getAllDefectsByHeaderId(proId);
        Integer code = allDefectsByHeaderId.isEmpty() ? ErrorCode.GET_FAIL : ErrorCode.GET_SUCCESS;
        String msg = allDefectsByHeaderId.isEmpty() ? "无数据" : "获取成功";
        return new Result(code, msg, allDefectsByHeaderId);
    }

    @GetMapping("/byCreat/{proId}")
    public Result getDefectByCreateId(@PathVariable("proId") Long proId) {
        List<Defect> allDefectsByHeaderId = defectService.getAllDefectsByCreatedId(proId);
        Integer code = allDefectsByHeaderId.isEmpty() ? ErrorCode.GET_FAIL : ErrorCode.GET_SUCCESS;
        String msg = allDefectsByHeaderId.isEmpty()? "无数据" : "获取成功";
        return new Result(code, msg, allDefectsByHeaderId);
    }

    @PostMapping
    public Result addDemand(@RequestBody Defect demand) {
        Boolean b = defectService.addDefect(demand);
        Integer code = b ? ErrorCode.ADD_SUCCESS : ErrorCode.ADD_FAIL;
        String msg = b ? "添加成功" : "添加失败";
        return new Result(code, msg, null);
    }

    @PutMapping("/changeStatus/{demandId}/{status}")
    public Result changeStatus(@PathVariable("demandId") Long demandId, @PathVariable("status") Integer status) {
        Boolean b = defectService.changeStatus(demandId, status);
        Integer code = b ? ErrorCode.UPDATE_SUCCESS : ErrorCode.UPDATE_FAIL;
        String msg = b ? "更新成功" : "更新失败";
        return new Result(code, msg, null);
    }

    @PutMapping("/changeHeadId/{demandId}/{headId}")
    public Result changeHeadId(@PathVariable("demandId") Long demandId, @PathVariable("headId") Long headId) {
        Boolean b = defectService.changeHeadId(demandId, headId);
        Integer code = b ? ErrorCode.UPDATE_SUCCESS : ErrorCode.UPDATE_FAIL;
        String msg = b ? "更新成功" : "更新失败";
        return new Result(code, msg, null);
    }

    @PutMapping("/changePriority/{demandId}/{priority}")
    public Result changePriority(@PathVariable("demandId") Long demandId, @PathVariable("priority") Integer priority) {
        Boolean b = defectService.changePriority(demandId, priority);
        Integer code = b ? ErrorCode.UPDATE_SUCCESS : ErrorCode.UPDATE_FAIL;
        String msg = b ? "更新成功" : "更新失败";
        return new Result(code, msg, null);
    }

    @PutMapping("/changeSeverity/{demandId}/{severity}")
    public Result changeSeverity(@PathVariable("demandId") Long demandId, @PathVariable("severity") Integer severity) {
        Boolean b = defectService.changeSeverity(demandId, severity);
        Integer code = b ? ErrorCode.UPDATE_SUCCESS : ErrorCode.UPDATE_FAIL;
        String msg = b ? "更新成功" : "更新失败";
        return new Result(code, msg, null);
    }

    @PutMapping("/changeProbability/{demandId}/{probability}")
    public Result changeProbability(@PathVariable("demandId") Long demandId, @PathVariable("probability") Integer robability) {
        Boolean b = defectService.changeProbability(demandId, robability);
        Integer code = b ? ErrorCode.UPDATE_SUCCESS : ErrorCode.UPDATE_FAIL;
        String msg = b ? "更新成功" : "更新失败";
        return new Result(code, msg, null);
    }

    @PutMapping("/changeType/{demandId}/{type}")
    public Result changeType(@PathVariable("demandId") Long demandId, @PathVariable("type") Long type) {
        Boolean b = defectService.changeType(demandId, type);
        Integer code = b ? ErrorCode.UPDATE_SUCCESS : ErrorCode.UPDATE_FAIL;
        String msg = b ? "更新成功" : "更新失败";
        return new Result(code, msg, null);
    }


    @PutMapping("/changeDesc")
    public Result changeDesc(@RequestBody Demand demand) {
        Boolean b = defectService.changeDesc(demand.getDemandId(), demand.getDemandDesc());
        Integer code = b ? ErrorCode.UPDATE_SUCCESS : ErrorCode.UPDATE_FAIL;
        String msg = b ? "更新成功" : "更新失败";
        return new Result(code, msg, null);
    }

    @PutMapping("/changeStartTime")
    public Result changeStartTime(@RequestBody Demand demand) {
        Boolean b = defectService.changeStartTime(demand.getDemandId(), demand.getStartTime());
        Integer code = b ? ErrorCode.UPDATE_SUCCESS : ErrorCode.UPDATE_FAIL;
        String msg = b ? "更新成功" : "更新失败";
        return new Result(code, msg, null);
    }

    @PutMapping("/changeEndTime")
    public Result changeEndTime(@RequestBody Demand demand) {
        Boolean b = defectService.changeEndTime(demand.getDemandId(), demand.getEndTime());
        Integer code = b ? ErrorCode.UPDATE_SUCCESS : ErrorCode.UPDATE_FAIL;
        String msg = b ? "更新成功" : "更新失败";
        return new Result(code, msg, null);
    }

    @GetMapping("/counts/{proId}")
    public Result getDemandCounts(@PathVariable("proId") Long proId) {
        Map<String, Integer> count = defectService.getDefectCounts(proId);
        return Result.success(count);
    }
}
