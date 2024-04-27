package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.Result;
import com.spms.entity.ProjectCost;
import com.spms.entity.RatedTimeCost;
import com.spms.enums.ResultCode;
import com.spms.mapper.ProjectCostMapper;
import com.spms.mapper.RatedTimeCostMapper;
import com.spms.service.ProjectCostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.spms.enums.ResourceUseTimeUnit.DAY;

@Service
public class ProjectCostServiceImpl extends ServiceImpl<ProjectCostMapper, ProjectCost> implements ProjectCostService {

    @Autowired
    private RatedTimeCostMapper ratedTimeCostMapper;

    @Override
    public Result estimateCost(List<ProjectCost> projectCosts) {
        if (projectCosts == null || projectCosts.isEmpty()) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        for (ProjectCost projectCost : projectCosts) {
            if (projectCost.getProjectId() == null || projectCost.getResourceId() == null || projectCost.getEstimateUseTime() == null || projectCost.getTimeUnit() == null) {
                return Result.fail(ResultCode.FAIL.getCode(), "非法操作");
            }
        }

        BigDecimal totalEstimateCost = BigDecimal.ZERO;

        for (ProjectCost projectCost : projectCosts) {
            LambdaQueryWrapper<RatedTimeCost> ratedTimeCostLambdaQueryWrapper = new LambdaQueryWrapper<>();
            ratedTimeCostLambdaQueryWrapper.eq(RatedTimeCost::getResourceId, projectCost.getResourceId());
            RatedTimeCost ratedTimeCost = ratedTimeCostMapper.selectOne(ratedTimeCostLambdaQueryWrapper);

            Integer estimateUesTime = projectCost.getEstimateUseTime();
            BigDecimal estimateCost;

            Integer timeUnit = projectCost.getTimeUnit();
            if (DAY.getCode().equals(timeUnit)) {
                BigDecimal dailyCost = ratedTimeCost.getDailyCost();
                estimateCost = BigDecimal.valueOf(estimateUesTime).multiply(dailyCost);
            } else {
                BigDecimal monthlyCost = ratedTimeCost.getMonthlyCost();
                estimateCost = BigDecimal.valueOf(estimateUesTime).multiply(monthlyCost);
            }

            projectCost.setEstimateCost(estimateCost);
            totalEstimateCost = totalEstimateCost.add(estimateCost);
            this.save(projectCost);
        }

        return Result.success(totalEstimateCost);
    }
}
