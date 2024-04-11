package com.spms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.Result;
import com.spms.entity.RoleMenu;
import com.spms.mapper.RoleMenuMapper;
import com.spms.service.RoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {

    @Override
    public Result assignPermissions(Long roleId, List<Long> menuIds) {
        return null;
    }
}
