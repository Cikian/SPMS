package com.spms.service.impl;

import com.alibaba.fastjson.JSONObject;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.spms.constants.RedisConstants.ROLE_LIST;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    @Transactional
    public Result list(RoleDTO roleDTO, Integer page, Integer size) {
        String currentPageData = redisTemplate.opsForValue().get(ROLE_LIST + page);
        if (currentPageData != null) {
            return Result.success(JSONObject.parseObject(currentPageData, Page.class));
        }

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

        redisTemplate.opsForValue().set(ROLE_LIST + page, JSONObject.toJSONString(roleDTOPage));
        return Result.success(roleDTOPage);
    }

    @Override
    public Result add(Role role) {
        return null;
    }

    @Override
    public Result delete(Long[] ids) {
        return null;
    }
}
