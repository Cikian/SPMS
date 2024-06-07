package com.spms.service;

import com.spms.entity.Demand;

import java.util.List;

/**
 * @Title: DemandDependenceService
 * @Author Cikian
 * @Package com.spms.service
 * @Date 2024/6/3 下午4:07
 * @description: SPMS:
 */
public interface DemandDependenceService {
    void add(Long demandId, List<Long> demands);
    void delete(Long demandId);
}
