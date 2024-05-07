package com.spms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.entity.Requirement;
import com.spms.mapper.RequirementMapper;
import com.spms.service.RequirementService;
import org.springframework.stereotype.Service;

@Service
public class RequirementServiceImpl extends ServiceImpl<RequirementMapper, Requirement> implements RequirementService {
}
