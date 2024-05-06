package com.spms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.entity.TestPlan;
import com.spms.mapper.TestPlanMapper;
import com.spms.service.TestPlanService;
import org.springframework.stereotype.Service;

@Service
public class TestPlanServiceImpl extends ServiceImpl<TestPlanMapper, TestPlan> implements TestPlanService {
}
