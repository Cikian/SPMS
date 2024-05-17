package com.spms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spms.entity.Demand;

/**
 * @Title: DemandMapper
 * @Author Cikian
 * @Package com.spms.mapper
 * @Date 2024/5/8 下午10:04
 * @description: SPMS:
 */
public interface DemandMapper extends BaseMapper<Demand> {
    Integer countByProId(Long proId);
    Integer countByProIdWhereIsComplete(Long proId);
}
