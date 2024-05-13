package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.Result;
import com.spms.entity.TestPlan;
import com.spms.entity.TestReport;
import com.spms.enums.ResultCode;
import com.spms.mapper.TestReportMapper;
import com.spms.security.LoginUser;
import com.spms.service.TestReportService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static com.spms.constants.SystemConstants.DELETE;
import static com.spms.constants.SystemConstants.NOT_DELETE;

@Service
public class TestReportServiceImpl extends ServiceImpl<TestReportMapper, TestReport> implements TestReportService {
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

        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();

        LambdaUpdateWrapper<TestReport> testReportLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        testReportLambdaUpdateWrapper.set(TestReport::getDelFlag, DELETE)
                .eq(TestReport::getTestReportId, testReportId);
        boolean update = this.update(testReportLambdaUpdateWrapper);
        return update ? Result.success("删除成功") : Result.fail(ResultCode.FAIL.getCode(), "删除失败");
    }
}
