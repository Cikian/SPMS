package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.ProjectResourceDTO;
import com.spms.dto.Result;
import com.spms.entity.ProjectResource;
import com.spms.entity.RoleUser;
import com.spms.entity.User;
import com.spms.enums.ResourceType;
import com.spms.enums.ResultCode;
import com.spms.mapper.*;
import com.spms.service.ProjectResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.spms.constants.SystemConstants.NOT_DELETE;

@Service
public class ProjectResourceServiceImpl extends ServiceImpl<ProjectResourceMapper, ProjectResource> implements ProjectResourceService {

    @Autowired
    private RatedTimeCostMapper ratedTimeCostMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleUserMapper roleUserMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public Result getMembersByProId(Long projectId, String userName) {
        if (projectId == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }
        LambdaQueryWrapper<ProjectResource> projectResourceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        projectResourceLambdaQueryWrapper.eq(ProjectResource::getProjectId, projectId)
                .eq(ProjectResource::getResourceType, ResourceType.EMPLOYEE.getCode())
                .eq(ProjectResource::getActualCost, BigDecimal.ZERO);
        List<ProjectResource> projectResourceList = this.list(projectResourceLambdaQueryWrapper);

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.like(User::getNickName, userName);
        List<User> users = userMapper.selectList(userLambdaQueryWrapper);

        if (users == null || users.isEmpty()) {
            return Result.fail(ResultCode.FAIL.getCode(), "未找到该用户");
        }

        // 过滤出符合条件的用户
        projectResourceList = projectResourceList.stream().filter(item -> {
            for (User user : users) {
                if (item.getResourceId().equals(user.getUserId())) {
                    return true;
                }
            }
            return false;
        }).toList();

        List<ProjectResourceDTO> projectResourceDTOS = projectResourceList.stream().map(item -> {
            ProjectResourceDTO projectResourceDTO = new ProjectResourceDTO();
            User user = userMapper.selectById(item.getResourceId());

            LambdaQueryWrapper<RoleUser> roleUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
            roleUserLambdaQueryWrapper.eq(RoleUser::getUserId, item.getResourceId())
                    .eq(RoleUser::getDelFlag, NOT_DELETE);
            List<RoleUser> roleUsers = roleUserMapper.selectList(roleUserLambdaQueryWrapper);
            List<String> list = roleUsers.stream().map(ru -> roleMapper.selectById(ru.getRoleId()).getRemark()).toList();
            projectResourceDTO.setId(item.getProjectResourceId());
            projectResourceDTO.setRole(list);
            projectResourceDTO.setResourceId(item.getResourceId());
            projectResourceDTO.setResourceName(user.getNickName());
            projectResourceDTO.setEstimateStartTime(item.getEstimateStartTime());
            projectResourceDTO.setEstimateEndTime(item.getEstimateEndTime());

            return projectResourceDTO;
        }).toList();

        return Result.success(projectResourceDTOS);
    }

}
