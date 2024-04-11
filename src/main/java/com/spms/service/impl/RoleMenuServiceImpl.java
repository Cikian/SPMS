package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.Result;
import com.spms.entity.RoleMenu;
import com.spms.enums.ResultCode;
import com.spms.mapper.RoleMenuMapper;
import com.spms.service.RoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.spms.constants.SystemConstants.DELETE;
import static com.spms.constants.SystemConstants.NOT_DELETE;

@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {

    @Override
    @Transactional
    public Result assignPermissions(Long roleId, List<Long> menuIds) {
        if (roleId == null || menuIds == null || menuIds.isEmpty()) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        //删除角色之前分配的权限
        LambdaUpdateWrapper<RoleMenu> roleMenuLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        roleMenuLambdaUpdateWrapper.eq(RoleMenu::getRoleId, roleId)
                .set(RoleMenu::getDelFlag, DELETE);
        this.update(roleMenuLambdaUpdateWrapper);

        for (Long menuId : menuIds) {
            LambdaQueryWrapper<RoleMenu> roleMenuLambdaQueryWrapper = new LambdaQueryWrapper<>();
            roleMenuLambdaQueryWrapper.eq(RoleMenu::getRoleId, roleId)
                    .eq(RoleMenu::getMenuId, menuId);
            RoleMenu existingRoleMenu = this.getOne(roleMenuLambdaQueryWrapper);

            if (existingRoleMenu != null) {
                LambdaUpdateWrapper<RoleMenu> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(RoleMenu::getRoleId, roleId)
                        .eq(RoleMenu::getMenuId, menuId)
                        .set(RoleMenu::getDelFlag, NOT_DELETE);
                this.update(updateWrapper);
            } else {
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                roleMenu.setDelFlag(NOT_DELETE);
                this.save(roleMenu);
            }
        }

        return Result.success("权限分配成功");
    }
}
