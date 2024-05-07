package com.spms.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.Result;
import com.spms.entity.Requirement;
import com.spms.entity.TestPlan;
import com.spms.entity.User;
import com.spms.enums.ResultCode;
import com.spms.mapper.RequirementMapper;
import com.spms.mapper.TestPlanMapper;
import com.spms.mapper.UserMapper;
import com.spms.service.NotificationService;
import com.spms.service.TestPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class TestPlanServiceImpl extends ServiceImpl<TestPlanMapper, TestPlan> implements TestPlanService {

    @Autowired
    private RequirementMapper requirementMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    @Transactional
    public Result add(TestPlan testPlan) {
        if (testPlan == null || testPlan.getRequirementId() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        if (StrUtil.isEmpty(testPlan.getPlanName())) {
            return Result.fail(ResultCode.FAIL.getCode(), "计划名称不能为空");
        }

        if (StrUtil.isEmpty(testPlan.getPlanContent())) {
            return Result.fail(ResultCode.FAIL.getCode(), "计划内容不能为空");
        }

        if (StrUtil.isEmpty(testPlan.getSchedule())) {
            return Result.fail(ResultCode.FAIL.getCode(), "计划进度不能为空");
        }

        if (testPlan.getHead() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "请填写负责人");
        }

        LambdaQueryWrapper<Requirement> requirementLambdaQueryWrapper = new LambdaQueryWrapper<>();
        requirementLambdaQueryWrapper.eq(Requirement::getRequirementId, testPlan.getRequirementId());
        if (!requirementMapper.exists(requirementLambdaQueryWrapper)) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUserId, testPlan.getHead());
        if (!userMapper.exists(userLambdaQueryWrapper)) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        LambdaQueryWrapper<TestPlan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TestPlan::getRequirementId, testPlan.getRequirementId());
        if (this.exists(queryWrapper)) {
            return Result.fail(ResultCode.FAIL.getCode(), "该需求已存在测试计划");
        }

        boolean isSuccess = this.save(testPlan);
        if (!isSuccess) {
            return Result.fail(ResultCode.FAIL.getCode(), "添加失败");
        }

        Boolean addSuccess = notificationService.addNotification(testPlan.getHead(), "您有一个新的测试计划");
        if (!addSuccess) {
            return Result.fail(ResultCode.FAIL.getCode(), "添加失败");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("testPlanId", testPlan.getTestPlanId());
        map.put("planName", testPlan.getPlanName());
        return Result.success(map);
    }
}
