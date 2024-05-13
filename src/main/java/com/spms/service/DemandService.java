package com.spms.service;

import com.spms.entity.Demand;

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
    // 根据状态查询需求
    List<Demand> getAllDemandsByStatus(Integer status);
    // 根据类型查询需求
    List<Demand> getAllDemandsByType(Long type);
    // 根据史诗or特性or用户故事or任务查询需求
    List<Demand> getAllDemandsByDType(Integer dType);
    // 根据负责人ID查询需求
    List<Demand> getAllDemandsByHeaderId(Long headerId);
    // 根据创建人ID查询需求
    List<Demand> getAllDemandsByCreatedId(Long createdId);
    // 根据需求ID更新需求状态
    Boolean changeStatus(Long demandId, Integer status);
    // 根据需求ID更新负责人
    Boolean changeHeadId(Long demandId, Long headerId);
    // 根据需求ID更新优先级
    Boolean changePriority(Long demandId, Integer priority);
}
