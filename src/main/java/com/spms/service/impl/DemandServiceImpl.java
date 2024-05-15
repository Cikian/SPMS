package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.spms.entity.Demand;
import com.spms.mapper.DemandMapper;
import com.spms.service.DemandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
        if (demand.getFatherDemandId() == 0) {
            demand.setLevel(0);
        } else {
            Demand fatherDemand = demandMapper.selectById(demand.getFatherDemandId());
            demand.setLevel(fatherDemand.getLevel() + 1);
        }
        // 查询该项目一共有多少个需求，编号加1
        Integer demandCount = demandMapper.countByProId(proId);
        demand.setDemandNo(demandCount + 1);
        return demandMapper.insert(demand) > 0;
    }

    @Override
    public Map<String, List<Demand>> getAllDemandsByProId(Long proId) {
        LambdaQueryWrapper<Demand> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Demand::getProId, proId);
        List<Demand> allDemands = demandMapper.selectList(lqw);
        List<Demand> demandsByLevel = processDemands(allDemands);
        Map<String, List<Demand>> result = new HashMap<>();
        result.put("allDemands", allDemands);
        result.put("demandsByLevel", demandsByLevel);
        return result;
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

    @Override
    public Boolean changeStatus(Long demandId, Integer status) {
        LambdaUpdateWrapper<Demand> luw = new LambdaUpdateWrapper<>();
        luw.eq(Demand::getDemandId, demandId);
        luw.set(Demand::getDemandStatus, status);

        return demandMapper.update(null, luw) > 0;
    }

    @Override
    public Boolean changeHeadId(Long demandId, Long headerId) {
        LambdaUpdateWrapper<Demand> luw = new LambdaUpdateWrapper<>();
        luw.eq(Demand::getDemandId, demandId);
        luw.set(Demand::getHeadId, headerId);

        return demandMapper.update(null, luw) > 0;
    }

    @Override
    public Boolean changePriority(Long demandId, Integer priority) {
        LambdaUpdateWrapper<Demand> luw = new LambdaUpdateWrapper<>();
        luw.eq(Demand::getDemandId, demandId);
        luw.set(Demand::getPriority, priority);

        return demandMapper.update(null, luw) > 0;
    }

    @Override
    public Boolean changeDesc(Long demandId, String desc) {
        LambdaUpdateWrapper<Demand> luw = new LambdaUpdateWrapper<>();
        luw.eq(Demand::getDemandId, demandId);
        luw.set(Demand::getDemandDesc, desc);

        return demandMapper.update(null, luw) > 0;
    }

    @Override
    public Boolean changeType(Long demandId, Long type) {
        LambdaUpdateWrapper<Demand> luw = new LambdaUpdateWrapper<>();
        luw.eq(Demand::getDemandId, demandId);
        luw.set(Demand::getType, type);

        return demandMapper.update(null, luw) > 0;
    }

    @Override
    public Boolean changeSource(Long demandId, Long source) {
        LambdaUpdateWrapper<Demand> luw = new LambdaUpdateWrapper<>();
        luw.eq(Demand::getDemandId, demandId);
        luw.set(Demand::getSource, source);

        return demandMapper.update(null, luw) > 0;
    }

    @Override
    public List<Demand> getChildDemands(Long demandId) {
        LambdaQueryWrapper<Demand> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Demand::getFatherDemandId, demandId);
        return demandMapper.selectList(lqw);
    }

    private List<Demand> processDemands(List<Demand> demands) {
        // 分离出demands中元素的不同level的元素

        List<Demand> level0Demands = new ArrayList<>();
        List<Demand> level1Demands = new ArrayList<>();
        List<Demand> level2Demands = new ArrayList<>();
        List<Demand> level3Demands = new ArrayList<>();
        for (Demand demand : demands) {
            if (demand.getLevel() == 0) {
                level0Demands.add(demand);
            } else if (demand.getLevel() == 1) {
                level1Demands.add(demand);
            } else if (demand.getLevel() == 2) {
                level2Demands.add(demand);
            } else if (demand.getLevel() == 3) {
                level3Demands.add(demand);
            }
        }
        findParent(level2Demands, level3Demands);
        findParent(level1Demands, level2Demands);
        findParent(level0Demands, level1Demands);
        return level0Demands;

    }

    private void findParent(List<Demand> fatherDemands, List<Demand> childDemands) {
        for (Demand fatherDemand : fatherDemands) {
            Long fatherId = fatherDemand.getDemandId();
            List<Demand> children = new ArrayList<>();
            for (Demand childDemand : childDemands) {
                if (Objects.equals(childDemand.getFatherDemandId(), fatherId)) {
                    children.add(childDemand);
                }
            }
            fatherDemand.setChildren(children);
        }
    }
}
