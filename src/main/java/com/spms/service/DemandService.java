package com.spms.service;

import com.spms.entity.Demand;

import java.util.List;

/**
 * @Title: DemandService
 * @Author Cikian
 * @Package com.spms.service
 * @Date 2024/5/8 下午10:05
 * @description: SPMS: 需求（工作项）service
 */
public interface DemandService {
    Boolean addDemand(Demand demand);
    List<Demand> getAllDemandsByProId(Long proId);
    List<Demand> getAllDemandsByStatus(Integer status);
    List<Demand> getAllDemandsByType(Long type);

    // 根据史诗or特性or用户故事or任务查询需求
    List<Demand> getAllDemandsByDType(Integer dType);
    List<Demand> getAllDemandsByHeaderId(Long headerId);
    List<Demand> getAllDemandsByCreatedId(Long createdId);
}
