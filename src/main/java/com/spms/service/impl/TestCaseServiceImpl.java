package com.spms.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.Result;
import com.spms.entity.TestCase;
import com.spms.enums.ResultCode;
import com.spms.mapper.TestCaseMapper;
import com.spms.service.TestCaseService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TestCaseServiceImpl extends ServiceImpl<TestCaseMapper, TestCase> implements TestCaseService {

    @Override
    public Result add(TestCase testCase) {
        if (testCase.getTestPlanId() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        if (StrUtil.isEmpty(testCase.getCaseName())){
            return Result.fail(ResultCode.FAIL.getCode(), "用例名称不能为空");
        }

        if (StrUtil.isEmpty(testCase.getCaseContent())){
            return Result.fail(ResultCode.FAIL.getCode(), "用例内容不能为空");
        }

        if (Objects.isNull(testCase.getPriority())){
            return Result.fail(ResultCode.FAIL.getCode(), "优先级不能为空");
        }

        testCase.setStatus(false);
        testCase.setDelFlag(false);

        if (!this.save(testCase)){
            return Result.fail(ResultCode.FAIL.getCode(), "添加失败");
        }
        return Result.success("添加成功");
    }

    @Override
    public Result list(Long testPlanId) {
        if (testPlanId == null){
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        LambdaQueryWrapper<TestCase> testCaseLambdaQueryWrapper = new LambdaQueryWrapper<>();
        testCaseLambdaQueryWrapper.eq(TestCase::getTestPlanId, testPlanId);
        List<TestCase> testCaseList = this.list(testCaseLambdaQueryWrapper);
        return Result.success(testCaseList);
    }
}
