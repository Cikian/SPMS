package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.Result;
import com.spms.dto.RoleDTO;
import com.spms.entity.Role;
import com.spms.entity.RoleUser;
import com.spms.enums.ResultCode;
import com.spms.mapper.RoleMapper;
import com.spms.mapper.RoleUserMapper;
import com.spms.service.RoleUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.spms.constants.SystemConstants.DELETE;
import static com.spms.constants.SystemConstants.NOT_DELETE;

@Service
public class RoleUserServiceImpl extends ServiceImpl<RoleUserMapper, RoleUser> implements RoleUserService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    @Transactional
    public Result assignRole(Long userId, List<Long> roleIds) {
        if (userId == null || roleIds == null || roleIds.isEmpty()) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        // 删除用户之前的角色
        LambdaUpdateWrapper<RoleUser> deleteWrapper = new LambdaUpdateWrapper<>();
        deleteWrapper.eq(RoleUser::getUserId, userId)
                .set(RoleUser::getDelFlag, DELETE);
        this.update(deleteWrapper);

        // 重新分配角色
        for (Long roleId : roleIds) {
            LambdaQueryWrapper<RoleUser> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RoleUser::getUserId, userId)
                    .eq(RoleUser::getRoleId, roleId);
            RoleUser existingRoleUser = this.getOne(queryWrapper);

            if (existingRoleUser != null) {
                // 如果存在相同的role_id和user_id的记录，且del_flag为true，则更新del_flag为false
                if (existingRoleUser.getDelFlag()) {
                    existingRoleUser.setDelFlag(NOT_DELETE);
                    LambdaUpdateWrapper<RoleUser> roleUserLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                    roleUserLambdaUpdateWrapper.eq(RoleUser::getUserId, userId)
                            .eq(RoleUser::getRoleId, roleId)
                            .set(RoleUser::getDelFlag, NOT_DELETE);
                    this.update(roleUserLambdaUpdateWrapper);
                }
            } else {
                // 如果不存在相同的role_id和user_id的记录，则插入新记录
                RoleUser roleUser = new RoleUser();
                roleUser.setUserId(userId);
                roleUser.setRoleId(roleId);
                roleUser.setDelFlag(NOT_DELETE);
                this.save(roleUser);
            }
        }
        return Result.success("分配成功");
    }

    @Override
    public Result delete(Long roleId) {
        if (roleId == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        LambdaUpdateWrapper<RoleUser> deleteWrapper = new LambdaUpdateWrapper<>();
        deleteWrapper.eq(RoleUser::getRoleId, roleId)
                .set(RoleUser::getDelFlag, DELETE);
        this.update(deleteWrapper);
        return Result.success("删除成功");
    }

    @Override
    public Result queryUserHasRole(Long userId) {
        if (userId == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        LambdaQueryWrapper<RoleUser> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(RoleUser::getUserId, userId)
                .eq(RoleUser::getDelFlag, NOT_DELETE);
        List<RoleUser> roleUsers = this.list(userLambdaQueryWrapper);

        List<RoleDTO> roleDTOList = roleUsers.stream().map(roleUser -> {
            Long roleId = roleUser.getRoleId();
            Role role = roleMapper.selectById(roleId);
            RoleDTO roleDTO = new RoleDTO();
            BeanUtils.copyProperties(role, roleDTO);
            return roleDTO;
        }).toList();

        return Result.success(roleDTOList);
    }


}
