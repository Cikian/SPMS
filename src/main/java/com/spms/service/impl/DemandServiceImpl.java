package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.spms.entity.Demand;
import com.spms.entity.Project;
import com.spms.mapper.DemandMapper;
import com.spms.mapper.ProjectMapper;
import com.spms.security.LoginUser;
import com.spms.service.DemandActiveService;
import com.spms.service.DemandService;
import com.spms.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class DemandServiceImpl implements DemandService {
    @Autowired
    private DemandMapper demandMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private DemandActiveService demandActiveService;
    @Autowired
    private ProjectService projectService;

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

        Long projectId = demand.getProId();
        Boolean isProHeader = projectService.judgeIsProHeader(projectId);
        if (isProHeader){
            demand.setDemandStatus(0);
        }

        int i = demandMapper.insert(demand);
        String activeContent = "";
        if (demand.getWorkItemType() == 0){
            activeContent = "史诗";
        } else if (demand.getWorkItemType() == 1){
            activeContent = "特性";
        } else if (demand.getWorkItemType() == 2){
            activeContent = "用户故事";
        } else if (demand.getWorkItemType() == 3){
            activeContent = "任务";
        }

        demandActiveService.addActive("创建", activeContent, demand.getDemandId(), "", "");

        return i > 0;
    }

    @Override
    public Map<String, List<Demand>> getAllDemandsByProId(Long proId) {
        LambdaQueryWrapper<Demand> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Demand::getProId, proId);
        lqw.ne(Demand::getDemandStatus, -2);
        lqw.ne(Demand::getDemandStatus, -3);
        List<Demand> allDemands = demandMapper.selectList(lqw);

        List<Demand> demandsByLevel = processDemands(allDemands);
        Map<String, List<Demand>> result = new HashMap<>();
        result.put("allDemands", demandMapper.selectList(lqw));
        result.put("demandsByLevel", demandsByLevel);
        return result;
    }

    @Override
    public Map<String, List<Demand>> searchDemands(Long proId, String keyword) {
        LambdaQueryWrapper<Demand> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Demand::getProId, proId);
        lqw.ne(Demand::getDemandStatus, -2);
        lqw.ne(Demand::getDemandStatus, -3);
        lqw.like(Demand::getTitle, keyword).or().like(Demand::getDemandNo, keyword);

        Map<String, List<Demand>> result = new HashMap<>();
        result.put("allDemands", demandMapper.selectList(lqw));
        return result;
    }

    @Override
    public Demand getDemandById(Long demandId) {
        Demand demand = demandMapper.selectById(demandId);
        return getChildren(demand);
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
    public List<Demand> getAllDemandsByHeaderId(Long proId) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();
        LambdaQueryWrapper<Demand> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Demand::getHeadId, userId);
        lqw.eq(Demand::getProId, proId);
        lqw.ne(Demand::getDemandStatus, -2);
        lqw.ne(Demand::getDemandStatus, -3);

        return demandMapper.selectList(lqw);
    }

    @Override
    public List<Demand> getAllDemandsByCreatedId(Long proId) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();
        LambdaQueryWrapper<Demand> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Demand::getCreateBy, userId);
        lqw.eq(Demand::getProId, proId);

        return demandMapper.selectList(lqw);
    }

    @Transactional
    @Override
    public Boolean changeStatus(Long demandId, Integer status) {
        LambdaUpdateWrapper<Demand> luw = new LambdaUpdateWrapper<>();
        luw.eq(Demand::getDemandId, demandId);
        luw.set(Demand::getDemandStatus, status);
        Demand oldDemand = demandMapper.selectById(demandId);
        int update = demandMapper.update(null, luw);
        Demand newDemand = demandMapper.selectById(demandId);
        if (oldDemand.getDemandStatus() != -2 && oldDemand.getDemandStatus() != -3) {
            demandActiveService.addActive("修改", "状态", demandId, oldDemand.getDemandStatus().toString(), newDemand.getDemandStatus().toString());
        }
        return update > 0;
    }

    @Override
    public Boolean changeHeadId(Long demandId, Long headerId) {
        LambdaUpdateWrapper<Demand> luw = new LambdaUpdateWrapper<>();
        luw.eq(Demand::getDemandId, demandId);
        luw.set(Demand::getHeadId, headerId);
        Demand oldDemand = demandMapper.selectById(demandId);
        int update = demandMapper.update(null, luw);
        Demand newDemand = demandMapper.selectById(demandId);

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
        LambdaUpdateWrapper<Demand> luw = new LambdaUpdateWrapper<>();
        luw.eq(Demand::getDemandId, demandId);
        luw.set(Demand::getPriority, priority);
        Demand oldDemand = demandMapper.selectById(demandId);
        int update = demandMapper.update(null, luw);
        Demand newDemand = demandMapper.selectById(demandId);
        demandActiveService.addActive("修改", "优先级", demandId, oldDemand.getPriority().toString(), newDemand.getPriority().toString());

        return update > 0;
    }

    @Override
    public Boolean changeDesc(Long demandId, String desc) {
        LambdaUpdateWrapper<Demand> luw = new LambdaUpdateWrapper<>();
        luw.eq(Demand::getDemandId, demandId);
        luw.set(Demand::getDemandDesc, desc);
        int update = demandMapper.update(null, luw);
        demandActiveService.addActive("修改", "描述", demandId, "", "");

        return update > 0;
    }

    @Override
    public Boolean changeStartTime(Long demandId, LocalDateTime startTime) {
        System.out.println("changeStartTime" + startTime);
        System.out.println("id: " + demandId);
        LambdaUpdateWrapper<Demand> luw = new LambdaUpdateWrapper<>();
        luw.eq(Demand::getDemandId, demandId);
        luw.set(Demand::getStartTime, startTime);
        Demand oldDemand = demandMapper.selectById(demandId);
        int update = demandMapper.update(null, luw);
        Demand newDemand = demandMapper.selectById(demandId);

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

        LambdaUpdateWrapper<Demand> luw = new LambdaUpdateWrapper<>();
        luw.eq(Demand::getDemandId, demandId);
        luw.set(Demand::getEndTime, endTime);
        Demand oldDemand = demandMapper.selectById(demandId);
        int update = demandMapper.update(null, luw);
        Demand newDemand = demandMapper.selectById(demandId);

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
        LambdaUpdateWrapper<Demand> luw = new LambdaUpdateWrapper<>();
        luw.eq(Demand::getDemandId, demandId);
        luw.set(Demand::getType, type);
        Demand oldDemand = demandMapper.selectById(demandId);
        int update = demandMapper.update(null, luw);
        Demand newDemand = demandMapper.selectById(demandId);

        String oldType = "";
        String newType = "";
        if (oldDemand.getType() != null){
            oldType = oldDemand.getType().toString();
        }
        if (newDemand.getType() != null){
            newType = newDemand.getType().toString();
        }
        demandActiveService.addActive("修改", "需求类型", demandId, oldType, newType);

        return update > 0;
    }

    @Override
    public Boolean changeSource(Long demandId, Long source) {
        LambdaUpdateWrapper<Demand> luw = new LambdaUpdateWrapper<>();
        luw.eq(Demand::getDemandId, demandId);
        luw.set(Demand::getSource, source);
        Demand oldDemand = demandMapper.selectById(demandId);
        int update = demandMapper.update(null, luw);
        Demand newDemand = demandMapper.selectById(demandId);

        String oldDemandSource = "";
        String newDemandSource = "";
        if (oldDemand.getSource() != null){
            oldDemandSource = oldDemand.getSource().toString();
        }
        if (newDemand.getSource() != null){
            newDemandSource = newDemand.getSource().toString();
        }
        demandActiveService.addActive("修改", "需求来源", demandId, oldDemandSource, newDemandSource);

        return update > 0;
    }

    @Override
    public List<Demand> getChildDemands(Long demandId) {
        LambdaQueryWrapper<Demand> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Demand::getFatherDemandId, demandId);
        return demandMapper.selectList(lqw);
    }

    @Override
    public Map<String, Integer> getDemandCounts(Long proId) {
        Integer all = demandMapper.countByProId(proId);
        Integer completed = demandMapper.countByProIdWhereIsComplete(proId);
        Map<String, Integer> result = new HashMap<>();
        result.put("all", all);
        result.put("completed", completed);
        return result;
    }

    @Override
    public Map<String, Project> getMyDemands() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();
        LambdaQueryWrapper<Demand> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Demand::getHeadId, userId);
        lqw.eq(Demand::getDemandStatus, 1);
        List<Demand> demands = demandMapper.selectList(lqw);

        // 获取demands中元素的不同pro_id

        Set<Long> proIds = new HashSet<>();
        for (Demand demand : demands) {
            proIds.add(demand.getProId());
        }
        System.out.println("proIds: " + proIds);

        Map<String, Project> proIdToDemands = new HashMap<>();
        for (Long proId : proIds) {
            LambdaQueryWrapper<Project> lqw1 = new LambdaQueryWrapper<>();
            lqw1.eq(Project::getProId, proId);
            Project project = projectMapper.selectOne(lqw1);
            List<Demand> demandGroupByProId = demands.stream().filter(d -> d.getProId().equals(proId)).toList();
            project.setDemands(demandGroupByProId);
            proIdToDemands.put(proId.toString(), project);
        }
        return proIdToDemands;
    }

    @Override
    public Map<String, Project> getMyHeaderDemands() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();
        LambdaQueryWrapper<Demand> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Demand::getHeadId, userId);
        List<Demand> demands = demandMapper.selectList(lqw);

        // 获取demands中元素的不同pro_id

        Set<Long> proIds = new HashSet<>();
        for (Demand demand : demands) {
            proIds.add(demand.getProId());
        }
        System.out.println("proIds: " + proIds);

        Map<String, Project> proIdToDemands = new HashMap<>();
        for (Long proId : proIds) {
            LambdaQueryWrapper<Project> lqw1 = new LambdaQueryWrapper<>();
            lqw1.eq(Project::getProId, proId);
            Project project = projectMapper.selectOne(lqw1);
            List<Demand> demandGroupByProId = demands.stream().filter(d -> d.getProId().equals(proId)).toList();
            project.setDemands(demandGroupByProId);
            proIdToDemands.put(proId.toString(), project);
        }
        return proIdToDemands;
    }

    @Override
    public Map<String, List<Demand>> getAuditByProId(Long proId) {
        LambdaQueryWrapper<Demand> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Demand::getProId, proId);
        lqw.eq(Demand::getDemandStatus, -2);
        List<Demand> allDemands = demandMapper.selectList(lqw);

        List<Demand> demandsByLevel = processDemands(allDemands);
        Map<String, List<Demand>> result = new HashMap<>();
        result.put("allDemands", demandMapper.selectList(lqw));
        result.put("demandsByLevel", demandsByLevel);
        return result;
    }

    @Override
    public Boolean deleteDemand(Long demandId) {
        int i = demandMapper.deleteById(demandId);
        return i > 0;
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

    Demand getChildren(Demand demand){
        LambdaQueryWrapper<Demand> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Demand::getFatherDemandId, demand.getDemandId());
        List<Demand> children = demandMapper.selectList(lqw);
        if (!children.isEmpty()){
            List<Demand> childrenSChildren = new ArrayList<>();
            for (Demand child : children){
                Demand children1 = getChildren(child);
                childrenSChildren.add(children1);
            }
            demand.setChildren(childrenSChildren);
        }
        return demand;
    }
}
