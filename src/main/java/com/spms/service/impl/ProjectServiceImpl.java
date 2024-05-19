package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.*;
import com.spms.entity.Project;
import com.spms.entity.ProjectResource;
import com.spms.entity.RatedTimeCost;
import com.spms.enums.ResourceType;
import com.spms.enums.ResultCode;
import com.spms.mapper.ProjectMapper;
import com.spms.mapper.ProjectResourceMapper;
import com.spms.mapper.RatedTimeCostMapper;
import com.spms.security.LoginUser;
import com.spms.service.ProjectService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
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
            pr.setResourceType(ResourceType.EMPLOYEE.getCode());
            pr.setEstimateStartTime(addProjectDTO.getExpectedStartTime());
            pr.setEstimateEndTime(addProjectDTO.getExpectedEndTime());
            pr.setUseType(1);
            pr.setActualCost(BigDecimal.ZERO);
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
    public ProjectDTO getProById(Long id) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();
        Project project = projectMapper.selectById(id);

        ProjectDTO projectDTO = new ProjectDTO();
        BeanUtils.copyProperties(project, projectDTO);
        projectDTO.setIsReviewer(userId.equals(project.getCreateBy()));

        return projectDTO;
    }

    @Override
    public Result addMember(AddProPeopleDTO addProPeopleDTO) {
        Project project = projectMapper.selectById(addProPeopleDTO.getProId());
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();
        if (!userId.equals(project.getCreateBy())) {
            return Result.fail(ResultCode.FAIL.getCode(), "您的权限不足");
        }
        ProjectResource projectResource = new ProjectResource();
        projectResource.setProjectId(addProPeopleDTO.getProId());
        projectResource.setResourceId(addProPeopleDTO.getMemberId());
        projectResource.setResourceType(ResourceType.EMPLOYEE.getCode());
        projectResource.setEstimateStartTime(addProPeopleDTO.getEstimateStartTime());
        projectResource.setEstimateEndTime(addProPeopleDTO.getEstimateEndTime());
        projectResource.setUseType(1);
        projectResource.setActualCost(BigDecimal.ZERO);
        LambdaQueryWrapper<RatedTimeCost> lqw = new LambdaQueryWrapper<>();
        lqw.eq(RatedTimeCost::getResourceId, addProPeopleDTO.getMemberId());

        long days = Duration.between(addProPeopleDTO.getEstimateStartTime(), addProPeopleDTO.getEstimateEndTime()).toDays();
        BigDecimal dailyCost = ratedTimeCostMapper.selectOne(lqw).getDailyCost();
        BigDecimal estimateCost = BigDecimal.valueOf(days).multiply(dailyCost);
        projectResource.setEstimateCost(estimateCost);
        projectResourceMapper.insert(projectResource);
        return Result.success();
    }

    @Override
    public Result deleteMember(DeleteProPeopleDTO deleteProPeopleDTO) {
        ProjectResource projectResource = projectResourceMapper.selectById(deleteProPeopleDTO.getId());

        Project project = projectMapper.selectById(projectResource.getProjectId());
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();
        if (!userId.equals(project.getCreateBy())) {
            return Result.fail(ResultCode.FAIL.getCode(), "您的权限不足");
        }

        //计算实际成本
        long days = Duration.between(deleteProPeopleDTO.getActualStartTime(), deleteProPeopleDTO.getActualEndTime()).toDays();
        LambdaQueryWrapper<RatedTimeCost> lqw = new LambdaQueryWrapper<>();
        lqw.eq(RatedTimeCost::getResourceId, projectResource.getResourceId());
        BigDecimal dailyCost = ratedTimeCostMapper.selectOne(lqw).getDailyCost();
        BigDecimal actualCost = BigDecimal.valueOf(days).multiply(dailyCost);

        projectResource.setActualStartTime(deleteProPeopleDTO.getActualStartTime());
        projectResource.setActualEndTime(deleteProPeopleDTO.getActualEndTime());
        projectResource.setActualCost(actualCost);
        projectResourceMapper.updateById(projectResource);
        return Result.success();
    }

    @Override
    public Boolean changeStatus(Long id, Integer status) {
        Project project = projectMapper.selectById(id);
        project.setProStatus(status);

        return projectMapper.updateById(project) > 0;
    }

    @Override
    public List<Project> getAllPro() {
        return projectMapper.selectList(null);
    }

}
