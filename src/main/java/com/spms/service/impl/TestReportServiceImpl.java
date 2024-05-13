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
import com.spms.service.TestReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static com.spms.constants.SystemConstants.DELETE;
import static com.spms.constants.SystemConstants.NOT_DELETE;

@Service
public class TestReportServiceImpl extends ServiceImpl<TestReportMapper, TestReport> implements TestReportService {

    @Autowired
    private TestPlanMapper testPlanMapper;

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

        return Result.success("删除成功");
    }
}
