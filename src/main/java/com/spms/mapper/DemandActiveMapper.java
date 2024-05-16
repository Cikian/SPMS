package com.spms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spms.entity.DemandActive;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Title: DemandActiveMapper
 * @Author Cikian
 * @Package com.spms.mapper
 * @Date 2024/5/16 上午2:14
 * @description: SPMS: 需求活动记录
 */

@Mapper
public interface DemandActiveMapper extends BaseMapper<DemandActive> {
}
