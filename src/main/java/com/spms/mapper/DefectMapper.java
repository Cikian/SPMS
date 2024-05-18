package com.spms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spms.entity.Defect;

/**
 * @Title: DefectMapper
 * @Author Cikian
 * @Package com.spms.mapper
 * @Date 2024/5/18 上午5:45
 * @description: SPMS: 缺陷
 */
public interface DefectMapper extends BaseMapper<Defect> {
    Integer countByProId(Long proId);
    Integer countByProIdWhereIsComplete(Long proId);
}
