package com.spms.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.Result;
import com.spms.dto.RoleDTO;
import com.spms.dto.UserDTO;
import com.spms.entity.Role;
import com.spms.entity.RoleUser;
import com.spms.entity.User;
import com.spms.enums.ResultCode;
import com.spms.handler.MyMetaObjectHandler;
import com.spms.mapper.RoleMapper;
import com.spms.mapper.RoleUserMapper;
import com.spms.mapper.UserMapper;
import com.spms.security.LoginUser;
import com.spms.service.RoleService;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.spms.constants.RedisConstants.ROLE_LIST;
import static com.spms.constants.SystemConstants.*;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RoleUserMapper roleUserMapper;

    @Override
    @Transactional
    public Result list(Integer page, Integer size) {
        String currentPageData = redisTemplate.opsForValue().get(ROLE_LIST + page + ":" + size);
        if (currentPageData != null) {
            return Result.success(JSONObject.parseObject(currentPageData, Page.class));
        }

        Page<Role> rolePage = new Page<>(page, size);
        Page<RoleDTO> roleDTOPage = new Page<>();

        LambdaQueryWrapper<Role> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleLambdaQueryWrapper.eq(Role::getDelFlag, NOT_DELETE);
        this.page(rolePage, roleLambdaQueryWrapper);

        if (rolePage.getRecords().isEmpty()) {
            return Result.fail(ResultCode.FAIL.getCode(), "暂无数据");
        }

        BeanUtils.copyProperties(rolePage, roleDTOPage, "records");

        List<RoleDTO> roleDTOList = rolePage.getRecords().stream().map(role -> {
            role.setRoleName(role.getRoleName().replace(ROLE_PREFIX, ""));
            RoleDTO roleDTO = new RoleDTO();
            BeanUtils.copyProperties(role, roleDTO);
            return roleDTO;
        }).toList();
        roleDTOPage.setRecords(roleDTOList);

        redisTemplate.opsForValue().set(ROLE_LIST + page + ":" + size, JSONObject.toJSONString(roleDTOPage));
        return Result.success(roleDTOPage);
    }

    @Override
    public Result add(Role role) {
        if (role == null || role.getRoleName() == null || role.getRemark() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        LambdaQueryWrapper<Role> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleLambdaQueryWrapper.eq(Role::getRoleName, role.getRoleName())
                .or()
                .eq(Role::getRemark, role.getRemark())
                .eq(Role::getDelFlag, NOT_DELETE);
        Role one = this.getOne(roleLambdaQueryWrapper);
        if (one != null) {
            return Result.fail(ResultCode.FAIL.getCode(), "角色标识或角色名称已存在，请重新输入");
        }

        if (!role.getRoleName().startsWith(ROLE_PREFIX)) {
            role.setRoleName(ROLE_PREFIX + role.getRoleName());
        }
        role.setDelFlag(NOT_DELETE);
        boolean isSuccess = this.save(role);

        if (!isSuccess) {
            return Result.fail(ResultCode.FAIL.getCode(), "添加失败");
        }

        Set<String> keys = redisTemplate.keys(ROLE_LIST + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        return Result.success("添加成功");
    }

    @Override
    public Result delete(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        LambdaQueryWrapper<RoleUser> roleUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleUserLambdaQueryWrapper.in(RoleUser::getRoleId, List.of(ids))
                .eq(RoleUser::getDelFlag, NOT_DELETE);
        List<RoleUser> roleUsers = roleUserMapper.selectList(roleUserLambdaQueryWrapper);

        if (roleUsers != null && !roleUsers.isEmpty()) {
            return Result.fail(ResultCode.FAIL.getCode(), "您要删除的角色中有用户关联，请先解除关联再删除");
        }

        LambdaUpdateWrapper<Role> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Role::getRoleId, List.of(ids))
                .set(Role::getDelFlag, DELETE);
        this.update(updateWrapper);

        Set<String> keys = redisTemplate.keys(ROLE_LIST + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        return Result.success("删除成功");
    }

    @Override
    public Result queryById(Long roleId) {
        if (roleId == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        LambdaQueryWrapper<Role> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleLambdaQueryWrapper.eq(Role::getRoleId, roleId)
                .eq(Role::getDelFlag, NOT_DELETE);
        Role role = this.getOne(roleLambdaQueryWrapper);

        if (role == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "角色不存在");
        }

        role.setRoleName(role.getRoleName().replace(ROLE_PREFIX, ""));

        RoleDTO roleDTO = new RoleDTO();
        BeanUtils.copyProperties(role, roleDTO);
        return Result.success(roleDTO);
    }

    @Override
    public Result updateStatus(Role role) {
        if (role == null || role.getRoleId() == null || role.getStatus() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        if (this.getById(role.getRoleId()) == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "角色不存在");
        }

        boolean isSuccess = this.updateById(role);

        if (!isSuccess) {
            return Result.fail(ResultCode.FAIL.getCode(), "更新失败");
        }

        Set<String> keys = redisTemplate.keys(ROLE_LIST + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        return Result.success("更新成功");
    }

    @Override
    public Result updateRoleInfo(Role role) {
        if (role == null || role.getRoleName() == null || role.getRemark() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        if (this.getById(role.getRoleId()) == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "角色不存在");
        }

        LambdaQueryWrapper<Role> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleLambdaQueryWrapper
                .ne(Role::getRoleId, role.getRoleId())
                .and(i -> i.eq(Role::getRoleName, role.getRoleName())
                        .or()
                        .eq(Role::getRemark, role.getRemark()))
                .eq(Role::getDelFlag, NOT_DELETE);
        Role one = this.getOne(roleLambdaQueryWrapper);
        if (one != null) {
            return Result.fail(ResultCode.FAIL.getCode(), "角色标识或角色名称已存在，请重新输入");
        }

        if (!role.getRoleName().startsWith(ROLE_PREFIX)) {
            role.setRoleName(ROLE_PREFIX + role.getRoleName());
        }

        this.updateById(role);

        Set<String> keys = redisTemplate.keys(ROLE_LIST + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        return Result.success("修改成功");
    }
}
