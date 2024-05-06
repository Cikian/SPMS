package com.spms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.entity.QualityTarget;
import com.spms.mapper.QualityTargetMapper;
import com.spms.service.QualityTargetService;
import org.springframework.stereotype.Service;

@Service
public class QualityTargetServiceImpl extends ServiceImpl<QualityTargetMapper, QualityTarget> implements QualityTargetService {
}
