package com.spms.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.Result;
import com.spms.dto.TestPlanDTO;
import com.spms.entity.*;
import com.spms.enums.ResourceType;
import com.spms.enums.ResultCode;
import com.spms.enums.ReviewStatus;
import com.spms.mapper.*;
import com.spms.security.LoginUser;
import com.spms.service.NotificationService;
import com.spms.service.TestPlanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.spms.constants.SystemConstants.NOT_DELETE;
import static com.spms.enums.ReviewStatus.*;
import static com.spms.enums.TestPlanStatus.*;

@Service
public class TestPlanServiceImpl extends ServiceImpl<TestPlanMapper, TestPlan> implements TestPlanService {
    @Autowired
    TestPlanMapper testPlanMapper;

    @Autowired
    private DemandMapper demandMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleUserMapper roleUserMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectResourceMapper projectResourceMapper;

    @Autowired
    private TestReportMapper testReportMapper;

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
            return Result.fail(ResultCode.FAIL.getCode(), "请选择负责人");
        }

        if (testPlan.getStartTime() == null || testPlan.getEndTime() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "请选择计划时间");
        }

        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();
        //获取测试计划对应的需求
        LambdaQueryWrapper<Demand> demandLambdaQueryWrapper = new LambdaQueryWrapper<>();
        demandLambdaQueryWrapper.eq(Demand::getDemandId, testPlan.getDemandId());
        Demand demand = demandMapper.selectOne(demandLambdaQueryWrapper);
        //获取需求对应的项目
        LambdaQueryWrapper<Project> projectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        projectLambdaQueryWrapper.eq(Project::getProId, demand.getProId());
        Project project = projectMapper.selectOne(projectLambdaQueryWrapper);
        //获取项目的所有成员
        LambdaQueryWrapper<ProjectResource> projectResourceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        projectResourceLambdaQueryWrapper.eq(ProjectResource::getProjectId, project.getProId());
        List<ProjectResource> projectResources = projectResourceMapper.selectList(projectResourceLambdaQueryWrapper);
        //过滤出项目中的测试人员，只有项目的测试人员才能添加测试计划
        List<ProjectResource> proAllMembers = projectResources.stream().filter(item -> Objects.equals(item.getResourceType(), ResourceType.EMPLOYEE.getCode())).toList();
        List<ProjectResource> proAllTestMember = proAllMembers.stream().filter(item -> {
            Long memberId = item.getResourceId();
            LambdaQueryWrapper<RoleUser> roleUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
            roleUserLambdaQueryWrapper.eq(RoleUser::getUserId, memberId);
            List<RoleUser> roleUsers = roleUserMapper.selectList(roleUserLambdaQueryWrapper);
            return roleUsers.stream().anyMatch(roleUser -> Objects.equals(roleUser.getRoleId(), 1777954267000070145L));
        }).toList();

        if (proAllTestMember.stream().noneMatch(item -> Objects.equals(item.getResourceId(), userId))) {
            return Result.fail(ResultCode.FAIL.getCode(), "您无权添加");
        }

        LambdaQueryWrapper<TestPlan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TestPlan::getDemandId, testPlan.getDemandId())
                .and(i -> i.eq(TestPlan::getReviewStatus, PENDING.getCode()).or().eq(TestPlan::getReviewStatus, PASS.getCode()));
        if (this.exists(queryWrapper)) {
            return Result.fail(ResultCode.FAIL.getCode(), "该需求已存在测试计划或者测试计划正在审核中");
        }

        testPlan.setReviewStatus(PENDING.getCode());
        testPlan.setProgress(0);
        testPlan.setDelFlag(NOT_DELETE);
        if (!this.save(testPlan)) {
            return Result.fail(ResultCode.FAIL.getCode(), "添加失败");
        }
        //发送审核通知给项目创建人
        Boolean addSuccess = notificationService
                .addNotification(project.getCreateBy(), testPlan.getPlanName() + "(" + project.getProName() + ")", "您有一条新的测试计划需要审核");
        if (!addSuccess) {
            return Result.fail(ResultCode.FAIL.getCode(), "添加失败");
        }
        return Result.success("添加成功");
    }

    @Override
    public Result list(TestPlan testPlan, Integer page, Integer size, Integer type, Integer status) {
        Page<TestPlan> testPlanPage = new Page<>(page, size);
        Page<TestPlanDTO> testPlanDTOPage = new Page<>();

        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();

        //type = 1 查询我负责的
        LambdaQueryWrapper<TestPlan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(!Objects.isNull(testPlan.getPlanName()), TestPlan::getPlanName, testPlan.getPlanName())
                .eq(Objects.equals(status, NOT_STARTED.getCode()), TestPlan::getProgress, 0)
                .between(Objects.equals(status, IN_PROGRESS.getCode()), TestPlan::getProgress, 1, 99)
                .eq(Objects.equals(status, COMPLETED.getCode()), TestPlan::getProgress, 100)
                .eq(type == 1, TestPlan::getHead, userId)
                .eq(TestPlan::getDelFlag, NOT_DELETE)
                .eq(TestPlan::getReviewStatus, PASS.getCode())
                .orderBy(true, false, TestPlan::getCreateTime)
                .select(TestPlan::getTestPlanId, TestPlan::getDemandId, TestPlan::getPlanName, TestPlan::getProgress, TestPlan::getHead, TestPlan::getStartTime, TestPlan::getEndTime);
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
                testPlanDTO.setHeadAvatar(finalUser.getAvatar());
            } else {
                LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
                userLambdaQueryWrapper.eq(User::getUserId, item.getHead());
                User head = userMapper.selectOne(userLambdaQueryWrapper);
                testPlanDTO.setHeadName(head.getNickName());
                testPlanDTO.setHeadAvatar(head.getAvatar());
            }

            LambdaQueryWrapper<Demand> demandLambdaQueryWrapper = new LambdaQueryWrapper<>();
            demandLambdaQueryWrapper.eq(Demand::getDemandId, item.getDemandId());
            Demand demand = demandMapper.selectOne(demandLambdaQueryWrapper);
            testPlanDTO.setProjectId(demand.getProId());
            return testPlanDTO;
        }).toList();

        testPlanDTOPage.setRecords(testPlanDTOList);
        return Result.success(testPlanDTOPage);
    }

    @Override
    public List<TestPlanDTO> listByProId(Long proId, String testPlanName, Integer status) {
        List<TestPlan> testPlans = testPlanMapper.selectListByProId(proId);

        testPlans = testPlans.stream().filter(item -> {
            if (StrUtil.isNotEmpty(testPlanName)) {
                return item.getPlanName().contains(testPlanName);
            }
            return true;
        }).filter(item -> {
            if (status != null) {
                if (status == 0) {
                    return true;
                } else if (status == 1) {
                    return item.getProgress() == 0;
                } else if (status == 2) {
                    return item.getProgress() > 0 && item.getProgress() < 100;
                } else {
                    return item.getProgress() == 100;
                }
            }
            return true;
        }).toList();

        return testPlans.stream().map(item -> {
            TestPlanDTO testPlanDTO = new TestPlanDTO();
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getUserId, item.getHead())
                    .select(User::getNickName);
            User user = userMapper.selectOne(userLambdaQueryWrapper);
            BeanUtils.copyProperties(item, testPlanDTO);
            testPlanDTO.setHeadName(user.getNickName());
            return testPlanDTO;
        }).toList();
    }

    @Override
    public Result queryById(Long id) {
        if (id == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        TestPlan testPlan = this.getById(id);
        if (testPlan == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "数据不存在");
        }

        TestPlanDTO testPlanDTO = new TestPlanDTO();
        BeanUtils.copyProperties(testPlan, testPlanDTO);

        LambdaQueryWrapper<Demand> demandLambdaQueryWrapper = new LambdaQueryWrapper<>();
        demandLambdaQueryWrapper.eq(Demand::getDemandId, testPlan.getDemandId());
        Demand demand = demandMapper.selectOne(demandLambdaQueryWrapper);
        testPlanDTO.setDemandName(demand.getTitle());

        LambdaQueryWrapper<Project> projectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        projectLambdaQueryWrapper.eq(Project::getProId, demand.getProId());
        Project project = projectMapper.selectOne(projectLambdaQueryWrapper);
        testPlanDTO.setProjectName(project.getProName());
        testPlanDTO.setProjectId(project.getProId());

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUserId, testPlan.getCreateBy())
                .select(User::getNickName);
        User user = userMapper.selectOne(userLambdaQueryWrapper);
        testPlanDTO.setCreatorName(user.getNickName());

        return Result.success(testPlanDTO);
    }

    @Override
    @Transactional
    public Result updateTestPlan(TestPlan testPlan) {
        if (testPlan == null || testPlan.getTestPlanId() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        TestPlan testPlan1 = this.getById(testPlan.getTestPlanId());
        if (testPlan1 == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "数据不存在");
        }

        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();

        if (!Objects.equals(testPlan1.getCreateBy(), userId)) {
            return Result.fail(ResultCode.FAIL.getCode(), "您无权修改");
        }

        if (StrUtil.isEmpty(testPlan.getPlanName())) {
            return Result.fail(ResultCode.FAIL.getCode(), "计划名称不能为空");
        }

        if (testPlan.getHead() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "请选择负责人");
        }

        if (!this.updateById(testPlan)) {
            return Result.fail(ResultCode.FAIL.getCode(), "修改失败");
        }

        //如果更新了负责人
        if (!Objects.equals(testPlan.getHead(), testPlan1.getHead())) {
            notificationService.addNotification(testPlan.getHead(), testPlan1.getPlanName(), "您有一条新的测试计划");
        }

        return Result.success("修改成功");
    }

    @Override
    public Result queryByDemandId(Long id) {
        if (id == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        LambdaQueryWrapper<TestPlan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TestPlan::getDemandId, id)
                .eq(TestPlan::getDelFlag, NOT_DELETE)
                .eq(TestPlan::getReviewStatus, PASS.getCode())
                .select(TestPlan::getTestPlanId, TestPlan::getPlanName, TestPlan::getProgress, TestPlan::getHead, TestPlan::getStartTime, TestPlan::getEndTime);
        List<TestPlan> testPlanList = this.list(queryWrapper);
        if (testPlanList.isEmpty()) {
            return Result.success(null);
        }

        List<Map<String, Object>> resultList = testPlanList.stream().map(item -> {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("testPlanId", item.getTestPlanId());
            resultMap.put("planName", item.getPlanName());
            resultMap.put("progress", item.getProgress());
            resultMap.put("head", item.getHead());
            resultMap.put("startTime", item.getStartTime());
            resultMap.put("endTime", item.getEndTime());
            return resultMap;
        }).toList();

        return Result.success(resultList);
    }

    @Override
    public Result listAllPendingByProId(Long proId) {
        if (proId == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        LambdaQueryWrapper<Demand> demandLambdaQueryWrapper = new LambdaQueryWrapper<>();
        demandLambdaQueryWrapper.eq(Demand::getProId, proId)
                .select(Demand::getDemandId);
        List<Demand> demands = demandMapper.selectList(demandLambdaQueryWrapper);
        if (demands.isEmpty()) {
            return Result.fail(ResultCode.FAIL.getCode(), "暂无数据");
        }
        List<Long> demandIds = demands.stream().map(Demand::getDemandId).toList();

        LambdaQueryWrapper<TestPlan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(TestPlan::getDemandId, demandIds)
                .eq(TestPlan::getReviewStatus, PENDING.getCode())
                .eq(TestPlan::getDelFlag, NOT_DELETE);
        List<TestPlan> testPlans = this.list(queryWrapper);
        if (testPlans.isEmpty()) {
            return Result.fail(ResultCode.FAIL.getCode(), "暂无数据");
        }

        List<TestPlanDTO> testPlanDTOList = testPlans.stream().map(item -> {
            TestPlanDTO testPlanDTO = new TestPlanDTO();
            BeanUtils.copyProperties(item, testPlanDTO);

            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getUserId, item.getHead())
                    .select(User::getNickName, User::getAvatar);
            User user = userMapper.selectOne(userLambdaQueryWrapper);

            LambdaQueryWrapper<User> userLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper1.eq(User::getUserId, item.getCreateBy())
                    .select(User::getNickName, User::getAvatar);
            User user1 = userMapper.selectOne(userLambdaQueryWrapper);

            LambdaQueryWrapper<Demand> demandLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            demandLambdaQueryWrapper1.eq(Demand::getDemandId, item.getDemandId())
                    .select(Demand::getTitle);
            Demand demand = demandMapper.selectOne(demandLambdaQueryWrapper1);

            testPlanDTO.setDemandName(demand.getTitle());
            testPlanDTO.setHeadName(user.getNickName());
            testPlanDTO.setHeadAvatar(user.getAvatar());
            testPlanDTO.setCreatorName(user1.getNickName());
            testPlanDTO.setCreatorAvatar(user1.getAvatar());
            return testPlanDTO;
        }).toList();

        return Result.success(testPlanDTOList);
    }

    @Override
    @Transactional
    public Result updateReviewStatus(Long testPlanId, Integer reviewResult) {
        if (testPlanId == null || reviewResult == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        TestPlan testPlan = this.getById(testPlanId);
        if (testPlan == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "数据不存在");
        }

        if (!Objects.equals(testPlan.getReviewStatus(), PENDING.getCode())) {
            return Result.fail(ResultCode.FAIL.getCode(), "该测试计划不在审核中");
        }

        if (Objects.equals(reviewResult, PASS.getCode())) {
            testPlan.setProgress(0);
        }
        //更新数据库
        testPlan.setReviewStatus(reviewResult);
        if (!this.updateById(testPlan)) {
            return Result.fail(ResultCode.FAIL.getCode(), "操作失败");
        }

        //发送审核结果通知给创建人，如果是通过，再工作通知发送给负责人
        Boolean addSuccess = notificationService
                .addNotification(testPlan.getCreateBy(), testPlan.getPlanName(),
                        "您的测试计划" + (Objects.equals(reviewResult, PASS.getCode()) ? "已通过审核" : "未通过审核"));
        if (!addSuccess) {
            return Result.fail(ResultCode.FAIL.getCode(), "操作失败");
        }

        if (Objects.equals(reviewResult, PASS.getCode())) {
            notificationService.addNotification(testPlan.getHead(), testPlan.getPlanName(), "您有一条新的测试计划");
        }
        return Result.success("操作成功");
    }

    @Override
    public Result listMySubmit(TestPlan testPlan, Integer page, Integer size, Integer reviewStatus) {
        Page<TestPlan> testPlanPage = new Page<>(page, size);
        Page<TestPlanDTO> testPlanDTOPage = new Page<>();
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();
        //根据传递的条件查询
        LambdaQueryWrapper<TestPlan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(!Objects.isNull(testPlan.getPlanName()), TestPlan::getPlanName, testPlan.getPlanName())
                .eq(Objects.equals(reviewStatus, PENDING.getCode()), TestPlan::getReviewStatus, PENDING.getCode())
                .eq(Objects.equals(reviewStatus, PASS.getCode()), TestPlan::getReviewStatus, PASS.getCode())
                .eq(Objects.equals(reviewStatus, REJECT.getCode()), TestPlan::getReviewStatus, REJECT.getCode())
                .eq(TestPlan::getCreateBy, userId)
                .eq(TestPlan::getDelFlag, NOT_DELETE)
                .orderBy(true, false, TestPlan::getCreateTime);
        this.page(testPlanPage, queryWrapper);
        if (testPlanPage.getRecords().isEmpty()) {
            return Result.fail(ResultCode.FAIL.getCode(), "暂无数据");
        }
        BeanUtils.copyProperties(testPlanPage, testPlanDTOPage, "records");

        List<TestPlanDTO> testPlanDTOList = testPlanPage.getRecords().stream().map(item -> {
            TestPlanDTO testPlanDTO = new TestPlanDTO();
            BeanUtils.copyProperties(item, testPlanDTO);

            LambdaQueryWrapper<Demand> demandLambdaQueryWrapper = new LambdaQueryWrapper<>();
            demandLambdaQueryWrapper.eq(Demand::getDemandId, item.getDemandId());
            Demand demand = demandMapper.selectOne(demandLambdaQueryWrapper);
            testPlanDTO.setDemandName(demand.getTitle());

            LambdaQueryWrapper<Project> projectLambdaQueryWrapper = new LambdaQueryWrapper<>();
            projectLambdaQueryWrapper.eq(Project::getProId, demand.getProId());
            Project project = projectMapper.selectOne(projectLambdaQueryWrapper);
            testPlanDTO.setProjectName(project.getProName());
            testPlanDTO.setProjectId(project.getProId());

            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getUserId, item.getHead())
                    .select(User::getNickName, User::getAvatar);
            User user = userMapper.selectOne(userLambdaQueryWrapper);
            testPlanDTO.setHeadName(user.getNickName());
            testPlanDTO.setHeadAvatar(user.getAvatar());

            return testPlanDTO;
        }).toList();

        testPlanDTOPage.setRecords(testPlanDTOList);

        return Result.success(testPlanDTOPage);
    }

    @Override
    public Result finishTestPlan(Long testPlanId) {
        if (testPlanId == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        TestPlan testPlan = this.getById(testPlanId);
        //判断当前用户是否是测试计划负责人
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();
        if (!userId.equals(testPlan.getHead())) {
            return Result.fail(ResultCode.FAIL.getCode(), "您无权修改");
        }
        //判断测试计划进度
        if (testPlan.getProgress() != 100) {
            return Result.fail(ResultCode.FAIL.getCode(), "该计划尚未完成，请完成后再进行存档");
        }
        //判断测试报告是否上传，审核是否通过
        LambdaQueryWrapper<TestReport> reportLambdaQueryWrapper = new LambdaQueryWrapper<>();
        reportLambdaQueryWrapper.eq(TestReport::getTestPlanId, testPlanId);
        TestReport testReport = testReportMapper.selectOne(reportLambdaQueryWrapper);
        if (testReport == null || testReport.getReviewStatus().equals(PENDING.getCode()) || testReport.getReviewStatus().equals(REJECT.getCode())) {
            return Result.fail(ResultCode.FAIL.getCode(), "请检查测试报告是否上传，审核是否通过");
        }
        testPlan.setIsArchive(true);
        if (!this.updateById(testPlan)) {
            return Result.fail(ResultCode.FAIL.getCode(), "存档失败");
        }
        return Result.success("存档成功");
    }

}
