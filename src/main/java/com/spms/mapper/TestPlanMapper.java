package com.spms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spms.entity.TestPlan;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TestPlanMapper extends BaseMapper<TestPlan> {
    List<TestPlan> selectListByProId(Long proId);
}
