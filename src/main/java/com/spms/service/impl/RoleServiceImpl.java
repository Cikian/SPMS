package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.Result;
import com.spms.dto.RoleDTO;
import com.spms.entity.Role;
import com.spms.enums.ResultCode;
import com.spms.mapper.RoleMapper;
import com.spms.service.RoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Override
    public Result list(RoleDTO roleDTO, Integer page, Integer size) {
        Page<Role> rolePage = new Page<>(page, size);
        Page<RoleDTO> roleDTOPage = new Page<>();

        LambdaQueryWrapper<Role> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleLambdaQueryWrapper.eq(!Objects.isNull(roleDTO.getRoleId()), Role::getRoleId, roleDTO.getRoleId())
                .eq(!Objects.isNull(roleDTO.getRoleName()), Role::getRoleName, roleDTO.getRoleName())
                .eq(!Objects.isNull(roleDTO.getStatus()), Role::getStatus, roleDTO.getStatus())
                .orderByAsc(Role::getCreateTime);
        this.page(rolePage, roleLambdaQueryWrapper);

        if (rolePage.getRecords().isEmpty()) {
            return Result.fail(ResultCode.FAIL.getCode(), "暂无数据");

        }

        BeanUtils.copyProperties(rolePage, roleDTOPage, "records");

        List<RoleDTO> roleDTOList = rolePage.getRecords().stream().map(role -> {
            RoleDTO roleDTO1 = new RoleDTO();
            BeanUtils.copyProperties(role, roleDTO1);
            return roleDTO1;
        }).toList();
        roleDTOPage.setRecords(roleDTOList);

        return Result.success(roleDTOPage);
    }
}
