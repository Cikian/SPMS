package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.spms.entity.Defect;
import com.spms.entity.Demand;
import com.spms.mapper.DefectMapper;
import com.spms.service.DefectService;
import com.spms.service.DemandActiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @Title: DemandServiceImpl
 * @Author Cikian
 * @Package com.spms.service.impl
 * @Date 2024/5/8 下午10:11
 * @description: SPMS:
 */

@Service
public class DefectServiceImpl implements DefectService {
    @Autowired
    private DefectMapper defectMapper;
    @Autowired
    private DemandActiveService demandActiveService;

    @Override
    public Boolean addDefect(Defect demand) {
        Long proId = demand.getProId();
        // 查询该项目一共有多少个需求，编号加1
        Integer demandCount = defectMapper.countByProId(proId);
        demand.setDemandNo(demandCount + 1);
        int i = defectMapper.insert(demand);
        demandActiveService.addActive("创建", "缺陷", demand.getDemandId(), "", "");

        return i > 0;
    }

    @Override
    public Map<String, List<Defect>> getAllDefectsByProId(Long proId) {
        LambdaQueryWrapper<Defect> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Defect::getProId, proId);
        List<Defect> defects = defectMapper.selectList(lqw);
        Map<String, List<Defect>> result = new HashMap<>();
        result.put("allDemands", defects);
        result.put("demandsByLevel", defects);
        return result;
    }

    @Override
    public Defect getDefectById(Long demandId) {
        return defectMapper.selectById(demandId);
    }


    @Override
    public List<Defect> getAllDefectsByStatus(Integer status) {
        return List.of();
    }

    @Override
    public List<Defect> getAllDefectsByType(Long type) {
        return List.of();
    }

    @Override
    public List<Defect> getAllDefectsByHeaderId(Long headerId) {
        return List.of();
    }

    @Override
    public List<Defect> getAllDefectsByCreatedId(Long createdId) {
        return List.of();
    }

    @Transactional
    @Override
    public Boolean changeStatus(Long demandId, Integer status) {
        LambdaUpdateWrapper<Defect> luw = new LambdaUpdateWrapper<>();
        luw.eq(Defect::getDemandId, demandId);
        luw.set(Defect::getDemandStatus, status);
        Defect oldDemand = defectMapper.selectById(demandId);
        int update = defectMapper.update(null, luw);
        Defect newDemand = defectMapper.selectById(demandId);
        demandActiveService.addActive("修改", "状态", demandId, oldDemand.getDemandStatus().toString(), newDemand.getDemandStatus().toString());

        return update > 0;
    }

    @Override
    public Boolean changeHeadId(Long demandId, Long headerId) {
        LambdaUpdateWrapper<Defect> luw = new LambdaUpdateWrapper<>();
        luw.eq(Defect::getDemandId, demandId);
        luw.set(Defect::getHeadId, headerId);
        Defect oldDemand = defectMapper.selectById(demandId);
        int update = defectMapper.update(null, luw);
        Defect newDemand = defectMapper.selectById(demandId);

        String oldHeaderId = "";
        String newHeaderId = "";
        if (oldDemand.getHeadId() != null){
            oldHeaderId = oldDemand.getHeadId().toString();
        }
        if (newDemand.getHeadId() != null){
            newHeaderId = newDemand.getHeadId().toString();
        }
        demandActiveService.addActive("修改", "负责人", demandId, oldHeaderId, newHeaderId);
        return update > 0;
    }

    @Override
    public Boolean changePriority(Long demandId, Integer priority) {
        LambdaUpdateWrapper<Defect> luw = new LambdaUpdateWrapper<>();
        luw.eq(Defect::getDemandId, demandId);
        luw.set(Defect::getPriority, priority);
        Defect oldDemand = defectMapper.selectById(demandId);
        int update = defectMapper.update(null, luw);
        Defect newDemand = defectMapper.selectById(demandId);
        demandActiveService.addActive("修改", "优先级", demandId, oldDemand.getPriority().toString(), newDemand.getPriority().toString());

        return update > 0;
    }

    @Override
    public Boolean changeSeverity(Long demandId, Integer severity) {
        LambdaUpdateWrapper<Defect> luw = new LambdaUpdateWrapper<>();
        luw.eq(Defect::getDemandId, demandId);
        luw.set(Defect::getSeverity, severity);
        Defect oldDemand = defectMapper.selectById(demandId);
        int update = defectMapper.update(null, luw);
        Defect newDemand = defectMapper.selectById(demandId);
        demandActiveService.addActive("修改", "严重程度", demandId, oldDemand.getSeverity().toString(), newDemand.getSeverity().toString());

        return update > 0;
    }

    @Override
    public Boolean changeProbability(Long demandId, Integer probability) {
        LambdaUpdateWrapper<Defect> luw = new LambdaUpdateWrapper<>();
        luw.eq(Defect::getDemandId, demandId);
        luw.set(Defect::getProbability, probability);
        Defect oldDemand = defectMapper.selectById(demandId);
        int update = defectMapper.update(null, luw);
        Defect newDemand = defectMapper.selectById(demandId);
        demandActiveService.addActive("修改", "复现概率", demandId, oldDemand.getProbability().toString(), newDemand.getProbability().toString());

        return update > 0;
    }

    @Override
    public Boolean changeDesc(Long demandId, String desc) {
        LambdaUpdateWrapper<Defect> luw = new LambdaUpdateWrapper<>();
        luw.eq(Defect::getDemandId, demandId);
        luw.set(Defect::getDemandDesc, desc);
        int update = defectMapper.update(null, luw);
        demandActiveService.addActive("修改", "描述", demandId, "", "");

        return update > 0;
    }

    @Override
    public Boolean changeStartTime(Long demandId, LocalDateTime startTime) {
        LambdaUpdateWrapper<Defect> luw = new LambdaUpdateWrapper<>();
        luw.eq(Defect::getDemandId, demandId);
        luw.set(Defect::getStartTime, startTime);
        Defect oldDemand = defectMapper.selectById(demandId);
        int update = defectMapper.update(null, luw);
        Defect newDemand = defectMapper.selectById(demandId);

        String fromTime = "";
        String toTime = "";
        if (oldDemand.getStartTime() != null){
            fromTime = oldDemand.getStartTime().toString();
        }
        if (newDemand.getStartTime() != null){
            toTime = newDemand.getStartTime().toString();
        }
        demandActiveService.addActive("修改", "开始时间", demandId, fromTime, toTime);

        return update > 0;
    }

    @Override
    public Boolean changeEndTime(Long demandId, LocalDateTime endTime) {
        System.out.println("更改结束时间id: " + demandId + " 结束时间: " + endTime);

        LambdaUpdateWrapper<Defect> luw = new LambdaUpdateWrapper<>();
        luw.eq(Defect::getDemandId, demandId);
        luw.set(Defect::getEndTime, endTime);
        Defect oldDemand = defectMapper.selectById(demandId);
        int update = defectMapper.update(null, luw);
        Defect newDemand = defectMapper.selectById(demandId);

        String fromTime = "";
        String toTime = "";
        if (oldDemand.getEndTime() != null){
            fromTime = oldDemand.getEndTime().toString();
        }
        if (newDemand.getEndTime() != null){
            toTime = newDemand.getEndTime().toString();
        }
        demandActiveService.addActive("修改", "结束时间", demandId, fromTime, toTime);
        return update > 0;
    }

    @Override
    public Boolean changeType(Long demandId, Long type) {
        LambdaUpdateWrapper<Defect> luw = new LambdaUpdateWrapper<>();
        luw.eq(Defect::getDemandId, demandId);
        luw.set(Defect::getType, type);
        Defect oldDemand = defectMapper.selectById(demandId);
        int update = defectMapper.update(null, luw);
        Defect newDemand = defectMapper.selectById(demandId);

        String oldType = "";
        String newType = "";
        if (oldDemand.getType() != null){
            oldType = oldDemand.getType().toString();
        }
        if (newDemand.getType() != null){
            newType = newDemand.getType().toString();
        }
        demandActiveService.addActive("修改", "缺陷类型", demandId, oldType, newType);

        return update > 0;
    }

    @Override
    public Map<String, Integer> getDefectCounts(Long proId) {
        Integer all = defectMapper.countByProId(proId);
        Integer completed = defectMapper.countByProIdWhereIsComplete(proId);
        return Map.of("all", all, "completed", completed);
    }
}
