package com.spms.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.Result;
import com.spms.dto.TestPlanDTO;
import com.spms.entity.Demand;
import com.spms.entity.Project;
import com.spms.entity.TestPlan;
import com.spms.entity.User;
import com.spms.enums.ResultCode;
import com.spms.mapper.DemandMapper;
import com.spms.mapper.ProjectMapper;
import com.spms.mapper.TestPlanMapper;
import com.spms.mapper.UserMapper;
import com.spms.security.LoginUser;
import com.spms.service.NotificationService;
import com.spms.service.TestPlanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.spms.constants.SystemConstants.NOT_DELETE;

@Service
public class TestPlanServiceImpl extends ServiceImpl<TestPlanMapper, TestPlan> implements TestPlanService {

    @Autowired
    private DemandMapper demandMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    @Transactional
    public Result add(TestPlan testPlan) {
        if (testPlan == null || testPlan.getDemandId() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        if (StrUtil.isEmpty(testPlan.getPlanName())) {
            return Result.fail(ResultCode.FAIL.getCode(), "计划名称不能为空");
        }

        if (testPlan.getHead() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "请填写负责人");
        }

        LambdaQueryWrapper<Demand> requirementLambdaQueryWrapper = new LambdaQueryWrapper<>();
        requirementLambdaQueryWrapper.eq(Demand::getDemandId, testPlan.getDemandId());
        if (!demandMapper.exists(requirementLambdaQueryWrapper)) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUserId, testPlan.getHead());
        if (!userMapper.exists(userLambdaQueryWrapper)) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        LambdaQueryWrapper<TestPlan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TestPlan::getDemandId, testPlan.getDemandId());
        if (this.exists(queryWrapper)) {
            return Result.fail(ResultCode.FAIL.getCode(), "该需求已存在测试计划");
        }

        LambdaQueryWrapper<Demand> demandLambdaQueryWrapper = new LambdaQueryWrapper<>();
        demandLambdaQueryWrapper.eq(Demand::getDemandId, testPlan.getDemandId());
        Demand demand = demandMapper.selectOne(demandLambdaQueryWrapper);

        LambdaQueryWrapper<Project> projectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        projectLambdaQueryWrapper.eq(Project::getProId, demand.getProId());
        Project project = projectMapper.selectOne(projectLambdaQueryWrapper);

        testPlan.setPlanContent("");
        testPlan.setProgress(0);
        boolean isSuccess = this.save(testPlan);
        if (!isSuccess) {
            return Result.fail(ResultCode.FAIL.getCode(), "添加失败");
        }

        Boolean addSuccess = notificationService.addNotification(testPlan.getHead(), testPlan.getPlanName() + "(" + project.getProName() + ")", "您有一个新的测试计划");
        if (!addSuccess) {
            return Result.fail(ResultCode.FAIL.getCode(), "添加失败");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("testPlanId", testPlan.getTestPlanId());
        map.put("planName", testPlan.getPlanName());
        return Result.success(map);
    }

    @Override
    public Result list(TestPlan testPlan, Integer page, Integer size, Integer type) {
        Page<TestPlan> testPlanPage = new Page<>(page, size);
        Page<TestPlanDTO> testPlanDTOPage = new Page<>();

        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();

        LambdaQueryWrapper<TestPlan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(!Objects.isNull(testPlan.getPlanName()), TestPlan::getPlanName, testPlan.getPlanName())
                .eq(TestPlan::getDelFlag, NOT_DELETE)
                .eq(type == 1, TestPlan::getHead, userId)
                .orderBy(true, false, TestPlan::getCreateTime)
                .select(TestPlan::getTestPlanId, TestPlan::getPlanName, TestPlan::getProgress, TestPlan::getHead, TestPlan::getStartTime, TestPlan::getEndTime);
        this.page(testPlanPage, queryWrapper);

        if (testPlanPage.getRecords().isEmpty()) {
            return Result.fail(ResultCode.FAIL.getCode(), "暂无数据");
        }

        BeanUtils.copyProperties(testPlanPage, testPlanDTOPage, "records");

        User user = null;
        if (type == 1) {
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getUserId, userId);
            user = userMapper.selectOne(userLambdaQueryWrapper);
        }
        final User finalUser = user;

        List<TestPlanDTO> testPlanDTOList = testPlanPage.getRecords().stream().map(item -> {
            TestPlanDTO testPlanDTO = new TestPlanDTO();
            BeanUtils.copyProperties(item, testPlanDTO);
            if (finalUser != null) {
                testPlanDTO.setHeadName(finalUser.getNickName());
            } else {
                LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
                userLambdaQueryWrapper.eq(User::getUserId, userId);
                User currentUser = userMapper.selectOne(userLambdaQueryWrapper);
                testPlanDTO.setHeadName(currentUser.getNickName());
            }
            return testPlanDTO;
        }).toList();

        testPlanDTOPage.setRecords(testPlanDTOList);
        return Result.success(testPlanDTOPage);
    }

}
