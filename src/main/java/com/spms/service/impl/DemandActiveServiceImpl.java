package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spms.entity.DemandActive;
import com.spms.mapper.DemandActiveMapper;
import com.spms.service.DemandActiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Title: DemandActiveServiceImpl
 * @Author Cikian
 * @Package com.spms.service.impl
 * @Date 2024/5/16 上午2:17
 * @description: SPMS: 需求活动
 */

@Service
public class DemandActiveServiceImpl implements DemandActiveService {
    @Autowired
    private DemandActiveMapper demandActiveMapper;

    @Override
    public void addActive(String activeType, String activeContent, Long demandId, String fromActive, String toActive) {
        DemandActive demandActive = new DemandActive();
        demandActive.setActiveType(activeType);
        demandActive.setActiveContent(activeContent);
        demandActive.setDemandId(demandId);
        if (activeType.equals("修改")){
            demandActive.setFromActive(fromActive);
            demandActive.setToActive(toActive);
        }
        demandActiveMapper.insert(demandActive);
    }

    @Override
    public List<DemandActive> getActiveListByDemandId(Long demandId) {
        LambdaQueryWrapper<DemandActive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DemandActive::getDemandId, demandId);
        lqw.orderByDesc(DemandActive::getCreateTime);
        return demandActiveMapper.selectList(lqw);
    }

    @Override
    public List<DemandActive> getActiveListByDemandIdAndComment(Long demandId, String content) {
        LambdaQueryWrapper<DemandActive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DemandActive::getDemandId, demandId);
        lqw.like(DemandActive::getActiveContent, content);
        lqw.orderByDesc(DemandActive::getCreateTime);
        return demandActiveMapper.selectList(lqw);
    }
}
