package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spms.entity.Demand;
import com.spms.entity.DemandDependence;
import com.spms.mapper.DemandDependenceMapper;
import com.spms.service.DemandDependenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Title: DemandDependenceServiceImpl
 * @Author Cikian
 * @Package com.spms.service.impl
 * @Date 2024/6/3 下午4:08
 * @description: SPMS:
 */
@Service
public class DemandDependenceServiceImpl implements DemandDependenceService {
    @Autowired
    private DemandDependenceMapper demandDependenceMapper;

    @Override
    public void add(Long demandId, List<Long> demands) {
        for (Long demand : demands) {
            DemandDependence dd = new DemandDependence();
            dd.setDependence(demandId);
            dd.setBeDependenceOn(demand);
            demandDependenceMapper.insert(dd);
        }
    }

    @Override
    public void delete(Long demandId) {
        LambdaQueryWrapper<DemandDependence> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DemandDependence::getDependence, demandId);
        demandDependenceMapper.delete(lqw);
    }
}
