package com.spms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spms.entity.QualityTargetRequirement;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QualityTargetRequirementMapper extends BaseMapper<QualityTargetRequirement> {
    List<QualityTargetRequirement> selectByProId(Long proId);
}
