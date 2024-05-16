package com.spms.service;

import com.spms.entity.DemandActive;

import java.util.List;

/**
 * @Title: DemandActiveService
 * @Author Cikian
 * @Package com.spms.service
 * @Date 2024/5/16 上午2:15
 * @description: SPMS: 需求活动记录
 */
public interface DemandActiveService {
    void addActive(String activeType, String activeContent, Long demandId, String fromActive, String toActive);
    List<DemandActive> getActiveListByDemandId(Long demandId);
    List<DemandActive> getActiveListByDemandIdAndComment(Long demandId, String content);
}
