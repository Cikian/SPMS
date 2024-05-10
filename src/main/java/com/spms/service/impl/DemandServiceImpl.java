package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spms.dto.Result;
import com.spms.entity.Demand;
import com.spms.mapper.DemandMapper;
import com.spms.service.DemandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Title: DemandServiceImpl
 * @Author Cikian
 * @Package com.spms.service.impl
 * @Date 2024/5/8 下午10:11
 * @description: SPMS:
 */

@Service
public class DemandServiceImpl implements DemandService {
    @Autowired
    private DemandMapper demandMapper;

    @Override
    public Boolean addDemand(Demand demand) {
        Long proId = demand.getProId();
        // 查询该项目一共有多少个需求，编号加1
        Integer demandCount = demandMapper.countByProId(proId);
        demand.setDemandNo(demandCount + 1);
        return demandMapper.insert(demand) > 0;
    }

    @Override
    public Result getAllDemandsByProId(Long proId) {
        LambdaQueryWrapper<Demand> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Demand::getProId, proId);
        List<Demand> demands = demandMapper.selectList(lqw);
        return Result.success(demands);
    }

    @Override
    public List<Demand> getAllDemandsByStatus(Integer status) {
        return List.of();
    }

    @Override
    public List<Demand> getAllDemandsByType(Long type) {
        return List.of();
    }

    @Override
    public List<Demand> getAllDemandsByDType(Integer dType) {
        return List.of();
    }

    @Override
    public List<Demand> getAllDemandsByHeaderId(Long headerId) {
        return List.of();
    }

    @Override
    public List<Demand> getAllDemandsByCreatedId(Long createdId) {
        return List.of();
    }


}
