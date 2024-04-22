package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.MenuDTO;
import com.spms.dto.Result;
import com.spms.entity.Menu;
import com.spms.entity.RoleMenu;
import com.spms.enums.ResultCode;
import com.spms.mapper.MenuMapper;
import com.spms.mapper.RoleMenuMapper;
import com.spms.service.RoleMenuService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.spms.constants.SystemConstants.DELETE;
import static com.spms.constants.SystemConstants.NOT_DELETE;

@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Override
    @Transactional
    public Result assignPermissions(Long roleId, List<Long> menuIds) {
        if (roleId == null) {
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

    @Override
    public Result queryRoleHasMenu(Long roleId) {
        if (roleId == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        LambdaQueryWrapper<RoleMenu> roleMenuLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleMenuLambdaQueryWrapper.eq(RoleMenu::getRoleId, roleId)
                .eq(RoleMenu::getDelFlag, NOT_DELETE);
        List<RoleMenu> roleHasMenuList = this.list(roleMenuLambdaQueryWrapper);

        List<MenuDTO> menuDTOList = roleHasMenuList.stream().map(roleMenu -> {
            Long menuId = roleMenu.getMenuId();
            Menu menu = menuMapper.selectById(menuId);
            MenuDTO menuDTO = new MenuDTO();
            BeanUtils.copyProperties(menu, menuDTO);
            return menuDTO;
        }).toList();

        return Result.success(menuDTOList);
    }
}
