package com.spms.service;

import com.spms.entity.Demand;
import com.spms.entity.Project;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Title: DemandService
 * @Author Cikian
 * @Package com.spms.service
 * @Date 2024/5/8 下午10:05
 * @description: SPMS: 需求（工作项）service
 */
public interface DemandService {
    // 添加需求
    Boolean addDemand(Demand demand);
    // 根据项目ID查询需求
    Map<String, List<Demand>> getAllDemandsByProId(Long proId);
    // 根据需求ID查询需求
    Demand getDemandById(Long demandId);
    // 根据状态查询需求
    List<Demand> getAllDemandsByStatus(Integer status);
    // 根据类型查询需求
    List<Demand> getAllDemandsByType(Long type);
    // 根据史诗or特性or用户故事or任务查询需求
    List<Demand> getAllDemandsByDType(Integer dType);
    // 根据负责人ID查询需求
    List<Demand> getAllDemandsByHeaderId(Long proId);
    // 根据创建人ID查询需求
    List<Demand> getAllDemandsByCreatedId(Long proId);
    // 根据需求ID更新需求状态
    Boolean changeStatus(Long demandId, Integer status);
    // 根据需求ID更新负责人
    Boolean changeHeadId(Long demandId, Long headerId);
    // 根据需求ID更新优先级
    Boolean changePriority(Long demandId, Integer priority);
    // 根据需求ID更新描述
    Boolean changeDesc(Long demandId, String desc);
    // 根据需求ID更新开始时间
    Boolean changeStartTime(Long demandId, LocalDateTime startTime);
    // 根据需求ID更新结束时间
    Boolean changeEndTime(Long demandId, LocalDateTime endTime);
    // 根据需求ID更新需求类型
    Boolean changeType(Long demandId, Long type);
    // 根据ID更新来源
    Boolean changeSource(Long demandId, Long source);
    // 根据ID获取子工作项
    List<Demand> getChildDemands(Long demandId);
    // 根据Id获取已完成和所有工作项的数量
    Map<String, Integer> getDemandCounts(Long proId);
    // 获取当前用户进行中的需求
    Map<String, Project> getMyDemands();
    // 获取当前用户负责的需求
    Map<String, Project> getMyHeaderDemands();

}
