package com.spms.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.Result;
import com.spms.dto.RoleDTO;
import com.spms.entity.Role;
import com.spms.entity.RoleUser;
import com.spms.enums.ResultCode;
import com.spms.mapper.RoleMapper;
import com.spms.mapper.RoleUserMapper;
import com.spms.security.LoginUser;
import com.spms.service.RoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.spms.constants.RedisConstants.ROLE_LIST;
import static com.spms.constants.SystemConstants.DELETE;
import static com.spms.constants.SystemConstants.NOT_DELETE;

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
            RoleDTO roleDTO1 = new RoleDTO();
            BeanUtils.copyProperties(role, roleDTO1);
            return roleDTO1;
        }).toList();
        roleDTOPage.setRecords(roleDTOList);

        redisTemplate.opsForValue().set(ROLE_LIST + page + ":" + size, JSONObject.toJSONString(roleDTOPage));
        return Result.success(roleDTOPage);
    }

    @Override
    public Result add(Role role) {
        if (role == null || role.getRoleName() == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        role.setCreateBy(loginUser.getUser().getUserId());
        role.setUpdateBy(loginUser.getUser().getUserId());
        role.setDelFlag(NOT_DELETE);
        role.setRemark(role.getRemark() == null ? "" : role.getRemark());

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
        roleUserLambdaQueryWrapper.in(RoleUser::getRoleId, ids)
                .eq(RoleUser::getDelFlag, NOT_DELETE);
        List<RoleUser> roleUsers = roleUserMapper.selectList(roleUserLambdaQueryWrapper);

        if (roleUsers != null && !roleUsers.isEmpty()) {
            return Result.fail(ResultCode.FAIL.getCode(), "您要删除的角色中有用户关联，请先解除关联再删除");
        }

        LambdaUpdateWrapper<Role> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Role::getRoleId, ids)
                .set(Role::getDelFlag, DELETE);
        this.update(updateWrapper);

        Set<String> keys = redisTemplate.keys(ROLE_LIST + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        return Result.success("删除成功");
    }
}
