package com.spms.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.Result;
import com.spms.entity.Demand;
import com.spms.entity.TestCase;
import com.spms.entity.TestPlan;
import com.spms.enums.ResultCode;
import com.spms.mapper.DemandMapper;
import com.spms.mapper.ProjectMapper;
import com.spms.mapper.TestCaseMapper;
import com.spms.mapper.TestPlanMapper;
import com.spms.security.LoginUser;
import com.spms.service.TestCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static com.spms.constants.SystemConstants.DELETE;
import static com.spms.constants.SystemConstants.NOT_DELETE;

@Service
public class TestCaseServiceImpl extends ServiceImpl<TestCaseMapper, TestCase> implements TestCaseService {

    @Autowired
    private TestPlanMapper testPlanMapper;

    @Autowired
    protected ProjectMapper projectMapper;

    @Autowired
    private DemandMapper demandMapper;

    @Override
    public Result add(TestCase testCase) {
        if (testCase.getTestPlanId() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        if (noPermission(testCase)) {
            return Result.fail(ResultCode.FAIL.getCode(), "无权限操作");
        }

        if (StrUtil.isEmpty(testCase.getCaseName())) {
            return Result.fail(ResultCode.FAIL.getCode(), "用例名称不能为空");
        }

        if (StrUtil.isEmpty(testCase.getCaseContent())) {
            return Result.fail(ResultCode.FAIL.getCode(), "用例内容不能为空");
        }

        if (Objects.isNull(testCase.getPriority())) {
            return Result.fail(ResultCode.FAIL.getCode(), "优先级不能为空");
        }

        LambdaQueryWrapper<TestCase> testCaseLambdaQueryWrapper = new LambdaQueryWrapper<>();
        testCaseLambdaQueryWrapper.eq(TestCase::getTestPlanId, testCase.getTestPlanId())
                .eq(TestCase::getCaseName, testCase.getCaseName());
        if (this.count(testCaseLambdaQueryWrapper) > 0) {
            return Result.fail(ResultCode.FAIL.getCode(), "用例名称已存在");
        }

        testCase.setStatus(false);
        testCase.setDelFlag(NOT_DELETE);

        if (!this.save(testCase)) {
            return Result.fail(ResultCode.FAIL.getCode(), "添加失败");
        }

        return updateProgress(testCase.getTestPlanId()) ? Result.success("添加成功") : Result.fail(ResultCode.FAIL.getCode(), "添加失败");
    }

    @Override
    public Result list(Long testPlanId) {
        if (testPlanId == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        LambdaQueryWrapper<TestCase> testCaseLambdaQueryWrapper = new LambdaQueryWrapper<>();
        testCaseLambdaQueryWrapper.eq(TestCase::getTestPlanId, testPlanId)
                .eq(TestCase::getDelFlag, NOT_DELETE);
        List<TestCase> testCaseList = this.list(testCaseLambdaQueryWrapper);
        return Result.success(testCaseList);
    }

    @Override
    public Result queryById(Long testCaseId) {
        if (testCaseId == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        TestCase testCase = this.getById(testCaseId);
        return Result.success(testCase);
    }

    @Override
    public Result update(TestCase testCase) {
        if (testCase == null || testCase.getTestCaseId() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        if (noPermission(testCase)) {
            return Result.fail(ResultCode.FAIL.getCode(), "无权限操作");
        }

        if (StrUtil.isEmpty(testCase.getCaseName())) {
            return Result.fail(ResultCode.FAIL.getCode(), "用例名称不能为空");
        }

        if (StrUtil.isEmpty(testCase.getCaseContent())) {
            return Result.fail(ResultCode.FAIL.getCode(), "用例内容不能为空");
        }

        if (Objects.isNull(testCase.getPriority())) {
            return Result.fail(ResultCode.FAIL.getCode(), "优先级不能为空");
        }

        LambdaQueryWrapper<TestCase> testCaseLambdaQueryWrapper = new LambdaQueryWrapper<>();
        testCaseLambdaQueryWrapper.eq(TestCase::getTestPlanId, testCase.getTestPlanId())
                .ne(TestCase::getTestCaseId, testCase.getTestCaseId())
                .eq(TestCase::getCaseName, testCase.getCaseName());
        if (this.count(testCaseLambdaQueryWrapper) > 0) {
            return Result.fail(ResultCode.FAIL.getCode(), "用例名称已存在");
        }

        if (!this.updateById(testCase)) {
            return Result.fail(ResultCode.FAIL.getCode(), "修改失败");
        }

        return updateProgress(testCase.getTestPlanId()) ? Result.success("修改成功") : Result.fail(ResultCode.FAIL.getCode(), "修改失败");
    }

    @Override
    public Result delete(Long testCaseId) {
        if (testCaseId == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        TestCase testCase = this.getById(testCaseId);
        if (testCase == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "数据不存在");
        }

        if (noPermission(testCase)) {
            return Result.fail(ResultCode.FAIL.getCode(), "无权限操作");
        }

        testCase.setDelFlag(DELETE);
        if (!this.updateById(testCase)) {
            return Result.fail(ResultCode.FAIL.getCode(), "删除失败");
        }

        return updateProgress(testCase.getTestPlanId()) ? Result.success("删除成功") : Result.fail(ResultCode.FAIL.getCode(), "删除失败");
    }

    @Override
    public Result calcProTestProgress(Long projectId) {
        if (projectId == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        LambdaQueryWrapper<Demand> demandLambdaQueryWrapper = new LambdaQueryWrapper<>();
        demandLambdaQueryWrapper.eq(Demand::getProId, projectId);
        List<Demand> demandList = demandMapper.selectList(demandLambdaQueryWrapper);

        float progress;
        int total = 0;
        int finish = 0;

        for (Demand demand : demandList) {
            //获取每个需求的测试计划
            LambdaQueryWrapper<TestPlan> testPlanLambdaQueryWrapper = new LambdaQueryWrapper<>();
            testPlanLambdaQueryWrapper.eq(TestPlan::getDemandId, demand.getDemandId())
                    .eq(TestPlan::getDelFlag, NOT_DELETE);
            TestPlan testPlan = testPlanMapper.selectOne(testPlanLambdaQueryWrapper);

            //如果没有测试计划
            if (testPlan == null){
                continue;
            }
            //获取每个测试计划的测试用例
            LambdaQueryWrapper<TestCase> testCaseLambdaQueryWrapper = new LambdaQueryWrapper<>();
            testCaseLambdaQueryWrapper.eq(TestCase::getTestPlanId, testPlan.getTestPlanId())
                    .eq(TestCase::getDelFlag, NOT_DELETE);
            List<TestCase> testCaseList = this.list(testCaseLambdaQueryWrapper);

            total += testCaseList.size();
            for (TestCase testCase : testCaseList) {
                if (testCase.getStatus()) {
                    finish++;
                }
            }
        }

        progress = (float) finish / total * 100;

        return Result.success(progress);
    }

    private boolean noPermission(TestCase testCase) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();

        LambdaQueryWrapper<TestPlan> testPlanLambdaQueryWrapper = new LambdaQueryWrapper<>();
        testPlanLambdaQueryWrapper.eq(TestPlan::getTestPlanId, testCase.getTestPlanId());
        TestPlan testPlan = testPlanMapper.selectOne(testPlanLambdaQueryWrapper);
        return !Objects.equals(testPlan.getHead(), userId);
    }

    private Boolean updateProgress(Long testPlanId) {
        LambdaQueryWrapper<TestCase> testCaseLambdaQueryWrapper = new LambdaQueryWrapper<>();
        testCaseLambdaQueryWrapper.eq(TestCase::getTestPlanId, testPlanId)
                .eq(TestCase::getDelFlag, NOT_DELETE);
        List<TestCase> testCaseList = this.list(testCaseLambdaQueryWrapper);

        float progress;

        int total = testCaseList.size();
        int finish = 0;
        for (TestCase testCase : testCaseList) {
            if (testCase.getStatus()) {
                finish++;
            }
        }
        progress = (float) finish / total * 100;

        LambdaUpdateWrapper<TestPlan> testPlanLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        testPlanLambdaUpdateWrapper.eq(TestPlan::getTestPlanId, testPlanId)
                .set(TestPlan::getProgress, progress);
        return testPlanMapper.update(testPlanLambdaUpdateWrapper) > 0;
    }
}
