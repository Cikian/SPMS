package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.Result;
import com.spms.entity.TestPlan;
import com.spms.entity.TestReport;
import com.spms.enums.ResultCode;
import com.spms.mapper.TestPlanMapper;
import com.spms.mapper.TestReportMapper;
import com.spms.security.LoginUser;
import com.spms.service.NotificationService;
import com.spms.service.TestReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import static com.spms.constants.SystemConstants.DELETE;
import static com.spms.constants.SystemConstants.NOT_DELETE;

@Service
public class TestReportServiceImpl extends ServiceImpl<TestReportMapper, TestReport> implements TestReportService {

    @Autowired
    private TestPlanMapper testPlanMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    public Result list(Long testPlanId) {
        if (testPlanId == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        LambdaQueryWrapper<TestReport> testReportLambdaQueryWrapper = new LambdaQueryWrapper<>();
        testReportLambdaQueryWrapper.eq(TestReport::getTestPlanId, testPlanId)
                .eq(TestReport::getDelFlag, NOT_DELETE);
        TestReport testReport = this.getOne(testReportLambdaQueryWrapper);

        if (testReport == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "暂未上传测试报告");
        }

        return Result.success(testReport);
    }

    @Override
    public Result delete(Long testReportId) {
        if (testReportId == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        TestReport testReport = this.getById(testReportId);
        Long testPlanId = testReport.getTestPlanId();
        TestPlan testPlan = testPlanMapper.selectById(testPlanId);

        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();

        if (!testPlan.getHead().equals(userId)) {
            return Result.fail(ResultCode.FAIL.getCode(), "无权限删除");
        }

        LambdaUpdateWrapper<TestReport> testReportLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        testReportLambdaUpdateWrapper.eq(TestReport::getTestReportId, testReportId)
                .set(TestReport::getDelFlag, DELETE);
        this.update(testReportLambdaUpdateWrapper);

        return Result.success("删除成功");
    }

    @Override
    public Result update(Long testReportId, Integer status) {
        TestReport testReport1 = this.getById(testReportId);
        Long testPlanId = testReport1.getTestPlanId();
        TestPlan testPlan = testPlanMapper.selectById(testPlanId);

        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();

        if (!testPlan.getCreateBy().equals(userId)) {
            return Result.fail(ResultCode.FAIL.getCode(), "无权限修改");
        }

        LambdaUpdateWrapper<TestReport> testReportLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        testReportLambdaUpdateWrapper.eq(TestReport::getTestReportId, testReportId)
                .set(TestReport::getReviewStatus, status);
        this.update(testReportLambdaUpdateWrapper);

        if (status == 1) {
            notificationService.addNotification(testPlan.getHead(), testPlan.getPlanName(), "您的测试报告已审核通过！");
        } else if (status == 2) {
            notificationService.addNotification(testPlan.getHead(), testPlan.getPlanName(), "您的测试报告未通过审核,请修改后重新上传！");
        }

        return Result.success("修改成功");
    }
}
