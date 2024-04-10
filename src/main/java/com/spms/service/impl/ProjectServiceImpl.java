package com.spms.service.impl;

import com.spms.dto.Result;
import com.spms.entity.Project;
import com.spms.entity.VO.ProDevice;
import com.spms.entity.VO.ProPeople;
import com.spms.entity.VO.ProjectVo;
import com.spms.mapper.ProjectMapper;
import com.spms.service.ProjectService;
import com.spms.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @Override
    public boolean addPro(ProjectVo projectVo) {
        Project project = new Project();

        project.setProName(projectVo.getProName());
        project.setProDesc(projectVo.getProDesc());
        project.setProFlag(projectVo.getProFlag());
        project.setProLeaderId(projectVo.getProLeaderId());
        project.setProType(projectVo.getProType());
        project.setProCustomer(projectVo.getProCustomer());
        project.setProCreateTime(TimeUtils.getNowTime());
        project.setProUpdateTime(TimeUtils.getNowTime());
        projectMapper.insert(project);
        Long proId = project.getProId();

        ProPeople[] members = projectVo.getProMembers();
        ProDevice[] devices = projectVo.getProDevices();
        for (ProPeople member : members) {
            member.setProId(proId);
            projectMapper.addCostPeople(member);
        }

        for (ProDevice device : devices) {
            device.setProId(proId);
            projectMapper.addCostDevice(device);
        }
        return true;
    }

}
