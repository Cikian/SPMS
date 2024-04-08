package com.spms.service.impl;

import com.spms.dto.Result;
import com.spms.entity.Project;
import com.spms.mapper.ProjectMapper;
import com.spms.service.ProjectService;
import com.spms.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Title: ProjectServiceImpl
 * @Author Cikian
 * @Package com.spms.service.impl
 * @Date 2024/4/8 下午1:54
 * @description: SPMS: 项目业务层实现类
 */

@Service
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    ProjectMapper projectMapper;

    @Override
    public boolean addPro(Project project) {
        project.setProCreateTime(TimeUtils.getNowTime());
        project.setProUpdateTime(TimeUtils.getNowTime());
        return projectMapper.insert(project) > 0;
    }
}
