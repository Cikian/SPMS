package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.ProjectResourceDTO;
import com.spms.dto.Result;
import com.spms.entity.*;
import com.spms.enums.ResourceType;
import com.spms.enums.ResultCode;
import com.spms.mapper.*;
import com.spms.service.ProjectResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.spms.constants.SystemConstants.NOT_DELETE;

@Service
public class ProjectResourceServiceImpl extends ServiceImpl<ProjectResourceMapper, ProjectResource> implements ProjectResourceService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleUserMapper roleUserMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private DictionaryDataMapper dictionaryDataMapper;
    @Autowired
    private ProjectResourceMapper projectResourceMapper;

    @Override
    public Result getMembersByProId(Long projectId, String userName) {
        if (projectId == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }
        // 查询符合条件的用户
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.like(User::getNickName, userName);
        List<User> users = userMapper.selectList(userLambdaQueryWrapper);
        // 如果没有符合条件的用户，则直接返回
        if (users == null || users.isEmpty()) {
            return Result.fail(ResultCode.FAIL.getCode(), "未找到目标用户");
        }
        // 查询项目中的所有员工
        LambdaQueryWrapper<ProjectResource> projectResourceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        projectResourceLambdaQueryWrapper.eq(ProjectResource::getProjectId, projectId)
                .eq(ProjectResource::getResourceType, ResourceType.EMPLOYEE.getCode())
                .eq(ProjectResource::getActualCost, BigDecimal.ZERO);
        List<ProjectResource> projectResourceList = this.list(projectResourceLambdaQueryWrapper);
        // 过滤出符合条件的项目员工
        projectResourceList = projectResourceList.stream().filter(item -> {
            for (User user : users) {
                if (item.getResourceId().equals(user.getUserId())) {
                    return true;
                }
            }
            return false;
        }).toList();
        // 封装数据
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
        BigDecimal totalCost = projectResourceList.stream().map(ProjectResource::getEstimateCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("totalCost",totalCost);
        resultMap.put("members",projectResourceDTOS);
        return Result.success(resultMap);
    }

    @Override
    public Result getDevicesByProId(Long proId, String deviceName) {
        if (proId == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        LambdaQueryWrapper<ProjectResource> projectResourceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        projectResourceLambdaQueryWrapper.eq(ProjectResource::getProjectId, proId)
                .eq(ProjectResource::getResourceType, ResourceType.DEVICE.getCode())
                .eq(ProjectResource::getActualCost, BigDecimal.ZERO);
        List<ProjectResource> projectResourceList = this.list(projectResourceLambdaQueryWrapper);

        LambdaQueryWrapper<Device> deviceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        deviceLambdaQueryWrapper.like(Device::getDevName, deviceName);
        List<Device> devices = deviceMapper.selectList(deviceLambdaQueryWrapper);

        if (devices == null || devices.isEmpty()) {
            return Result.fail(ResultCode.FAIL.getCode(), "未找到该设备");
        }

        projectResourceList = projectResourceList.stream().filter(item -> {
            for (Device device : devices) {
                if (item.getResourceId().equals(device.getDevId())) {
                    return true;
                }
            }
            return false;
        }).toList();

        List<ProjectResourceDTO> projectResourceDTOS = projectResourceList.stream().map(item -> {
            ProjectResourceDTO projectResourceDTO = new ProjectResourceDTO();
            Device device = deviceMapper.selectById(item.getResourceId());
            LambdaQueryWrapper<DictionaryData> dictionaryDataLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dictionaryDataLambdaQueryWrapper.eq(DictionaryData::getDictionaryDataId, device.getType());
            DictionaryData dictionaryData = dictionaryDataMapper.selectOne(dictionaryDataLambdaQueryWrapper);
            List<String> list = new ArrayList<>();
            list.add(dictionaryData.getLabel());
            projectResourceDTO.setId(item.getProjectResourceId());
            projectResourceDTO.setResourceId(item.getResourceId());
            projectResourceDTO.setRole(list);
            projectResourceDTO.setResourceName(device.getDevName());
            projectResourceDTO.setEstimateStartTime(item.getEstimateStartTime());
            projectResourceDTO.setEstimateEndTime(item.getEstimateEndTime());

            return projectResourceDTO;
        }).toList();
        return Result.success(projectResourceDTOS);
    }

    @Override
    public Result getMemberCostByProId(Long proId) {
        LambdaQueryWrapper<ProjectResource> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ProjectResource::getProjectId, proId);
        lqw.eq(ProjectResource::getResourceType, ResourceType.EMPLOYEE.getCode());
        List<ProjectResource> projectResources = projectResourceMapper.selectList(lqw);
        BigDecimal totalCost = projectResources.stream().map(ProjectResource::getEstimateCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        return Result.success(totalCost);
    }

}
