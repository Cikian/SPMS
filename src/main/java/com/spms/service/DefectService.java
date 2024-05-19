package com.spms.service;

import com.spms.entity.Defect;

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
public interface DefectService {
    // 添加需求
    Boolean addDefect(Defect defect);
    // 根据项目ID查询需求
    Map<String, List<Defect>> getAllDefectsByProId(Long proId);
    // 根据需求ID查询需求
    Defect getDefectById(Long demandId);
    // 根据状态查询需求
    List<Defect> getAllDefectsByStatus(Integer status);
    // 根据类型查询需求
    List<Defect> getAllDefectsByType(Long type);
    // 根据负责人ID查询需求
    List<Defect> getAllDefectsByHeaderId(Long proId);
    // 根据创建人ID查询需求
    List<Defect> getAllDefectsByCreatedId(Long proId);
    // 根据需求ID更新需求状态
    Boolean changeStatus(Long demandId, Integer status);
    // 根据需求ID更新负责人
    Boolean changeHeadId(Long demandId, Long headerId);
    // 根据需求ID更新优先级
    Boolean changePriority(Long demandId, Integer priority);
    // 根据需求ID更新优先级
    Boolean changeSeverity(Long demandId, Integer severity);
    // 根据需求ID更新优先级
    Boolean changeProbability(Long demandId, Integer probability);
    // 根据需求ID更新描述
    Boolean changeDesc(Long demandId, String desc);
    // 根据需求ID更新开始时间
    Boolean changeStartTime(Long demandId, LocalDateTime startTime);
    // 根据需求ID更新结束时间
    Boolean changeEndTime(Long demandId, LocalDateTime endTime);
    // 根据需求ID更新需求类型
    Boolean changeType(Long demandId, Long type);
    // 根据Id获取已完成和所有工作项的数量
    Map<String, Integer> getDefectCounts(Long proId);

}
