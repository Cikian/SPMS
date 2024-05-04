package com.spms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spms.entity.Project;
import com.spms.entity.vo.ProDevice;
import com.spms.dto.AddProPeopleDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
    int addCostPeople(AddProPeopleDTO aDDProPeopleDTO);
    int addCostDevice(ProDevice proDevice);
}
