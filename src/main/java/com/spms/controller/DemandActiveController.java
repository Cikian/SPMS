package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.DemandActive;
import com.spms.enums.ErrorCode;
import com.spms.service.DemandActiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Title: DemandActiveController
 * @Author Cikian
 * @Package com.spms.controller
 * @Date 2024/5/16 上午2:22
 * @description: SPMS: 需求活动
 */

@RestController
@RequestMapping("/demandActive")
public class DemandActiveController {
    @Autowired
    private DemandActiveService demandActiveService;

    @GetMapping
    public Result getDemandActiveList(@RequestParam("demandId") Long demandId) {
        System.out.println("获取活动列表" + demandId);
        List<DemandActive> activeListByDemandId = demandActiveService.getActiveListByDemandId(demandId);
        Integer code = !activeListByDemandId.isEmpty() ? ErrorCode.GET_SUCCESS : ErrorCode.GET_FAIL;
        String msg = !activeListByDemandId.isEmpty() ? "获取成功" : "获取失败";
        return new Result(code, msg, activeListByDemandId);
    }
}
