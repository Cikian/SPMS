package com.spms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.Result;
import com.spms.entity.Requirement;
import com.spms.entity.TestPlan;
import com.spms.enums.ResultCode;
import com.spms.mapper.RequirementMapper;
import com.spms.service.RequirementService;
import com.spms.service.TestPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequirementServiceImpl extends ServiceImpl<RequirementMapper, Requirement> implements RequirementService {

    @Autowired
    private TestPlanService testPlanService;

    @Override
    public Result add(Requirement requirement) {

        //TODO:添加需求
        boolean save = this.save(requirement);
        if (!save) {
            return Result.fail(ResultCode.FAIL.getCode(), "添加失败");
        }

        return null;
    }
}
