package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.Result;
import com.spms.entity.Project;
import com.spms.entity.ProjectResource;
import com.spms.entity.RatedTimeCost;
import com.spms.entity.User;
import com.spms.dto.AddProPeopleDTO;
import com.spms.dto.AddProjectDTO;
import com.spms.enums.ResultCode;
import com.spms.mapper.ProjectMapper;
import com.spms.mapper.ProjectResourceMapper;
import com.spms.mapper.RatedTimeCostMapper;
import com.spms.security.LoginUser;
import com.spms.service.ProjectService;
import com.spms.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @Title: ProjectServiceImpl
 * @Author Cikian
 * @Package com.spms.service.impl
 * @Date 2024/4/8 下午1:54
 * @description: SPMS: 项目业务层实现类
 */

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {
    @Autowired
    ProjectMapper projectMapper;
    @Autowired
    RatedTimeCostMapper ratedTimeCostMapper;
    @Autowired
    ProjectResourceMapper projectResourceMapper;

    @Transactional
    @Override
    public boolean addPro(AddProjectDTO addProjectDTO) {
        long days = Duration.between(addProjectDTO.getExpectedStartTime(), addProjectDTO.getExpectedEndTime()).toDays();

        Project project = new Project();

        project.setProName(addProjectDTO.getProName());
        project.setProDesc(addProjectDTO.getProDesc());
        project.setProFlag(addProjectDTO.getProFlag());
        project.setProType(addProjectDTO.getProType());
        project.setProCustomer(addProjectDTO.getProCustomer());
        project.setExpectedStartTime(addProjectDTO.getExpectedStartTime());
        project.setExpectedEndTime(addProjectDTO.getExpectedEndTime());
        projectMapper.insert(project);
        Long proId = project.getProId();

        Long[] membersIds = addProjectDTO.getProMembersIds();
        for (Long memberId : membersIds) {
            ProjectResource pr = new ProjectResource();
            pr.setProjectId(proId);
            pr.setResourceId(memberId);
            pr.setResourceType(0);
            pr.setEstimateStartTime(addProjectDTO.getExpectedStartTime());
            pr.setEstimateEndTime(addProjectDTO.getExpectedEndTime());
            pr.setUseType(1);
            LambdaQueryWrapper<RatedTimeCost> lqw = new LambdaQueryWrapper<>();
            lqw.eq(RatedTimeCost::getResourceId, memberId);

            BigDecimal dailyCost = ratedTimeCostMapper.selectOne(lqw).getDailyCost();
            BigDecimal estimateCost = BigDecimal.valueOf(days).multiply(dailyCost);
            System.out.println("预计成本：" + dailyCost);
            pr.setEstimateCost(estimateCost);
            projectResourceMapper.insert(pr);
        }
        return true;
    }

    @Override
    public List<Project> getProjectByStatus(Integer status) {
        LambdaQueryWrapper<Project> projectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        projectLambdaQueryWrapper.eq(!Objects.isNull(status), Project::getProFlag, status);
        return projectMapper.selectList(projectLambdaQueryWrapper);
    }

    @Override
    public List<Project> getAllPro() {
        return projectMapper.selectList(null);
    }

}
