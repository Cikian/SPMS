package com.spms.controller;

import com.spms.dto.Result;
import com.spms.dto.TestPlanDTO;
import com.spms.entity.QualityTarget;
import com.spms.entity.TestPlan;
import com.spms.enums.ErrorCode;
import com.spms.service.TestPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/testPlan")
public class TestPlanController {

    @Autowired
    private TestPlanService testPlanService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('testPlan:add') ||  hasRole('system_admin')")
    public Result add(@RequestBody TestPlan testPlan) {
        return testPlanService.add(testPlan);
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('testPlan:list') ||  hasRole('system_admin')")
    public Result list(@RequestBody TestPlan testPlan,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestParam(defaultValue = "0") Integer type,
                       @RequestParam(defaultValue = "0") Integer status) {
        return testPlanService.list(testPlan, page, size, type, status);
    }

    @GetMapping("/listByProId")
    public Result listByProId(@RequestParam("proId") Long proId,
                              @RequestParam("testPlanName") String testPlanName,
                              @RequestParam("status") Integer status) {
        List<TestPlanDTO> testPlans = testPlanService.listByProId(proId, testPlanName, status);
        Integer code = testPlans == null ? ErrorCode.GET_FAIL : ErrorCode.GET_SUCCESS;
        String msg = testPlans == null ? "获取失败" : "获取成功";
        return new Result(code, msg, testPlans);
    }

    @GetMapping("/listAllPendingByProId")
    public Result listAllPendingByProId(@RequestParam("proId") Long proId) {
        return testPlanService.listAllPendingByProId(proId);
    }

    @PostMapping("/listMySubmit")
    public Result listMySubmit(@RequestBody TestPlan testPlan,
                               @RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer size,
                               @RequestParam(defaultValue = "3") Integer reviewStatus) {
        return testPlanService.listMySubmit(testPlan,page,size,reviewStatus);
    }

    @GetMapping("/queryById/{id}")
    @PreAuthorize("hasAuthority('testPlan:queryById') ||  hasRole('system_admin')")
    public Result queryById(@PathVariable("id") Long id) {
        return testPlanService.queryById(id);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('testPlan:update') ||  hasRole('system_admin')")
    public Result update(@RequestBody TestPlan testPlan) {
        return testPlanService.updateTestPlan(testPlan);
    }

    @PostMapping("/updateReviewStatus/{testPlanId}/{reviewResult}")
    @PreAuthorize("hasAuthority('testPlan:update:review') ||  hasRole('system_admin')")
    public Result updateReviewStatus(@PathVariable Long testPlanId, @PathVariable Integer reviewResult) {
        return testPlanService.updateReviewStatus(testPlanId, reviewResult);
    }

    @GetMapping("/byDemand/{demandId}")
    public Result byDemand(@PathVariable("demandId") Long demandId) {
        return testPlanService.queryByDemandId(demandId);
    }
}
