package com.spms.controller;

import com.spms.dto.Result;
import com.spms.service.RoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roleMenu")
public class RoleMenuController {

    @Autowired
    private RoleMenuService roleMenuService;

    @PostMapping("/assignPermissions")
    @PreAuthorize("hasAuthority('sys:roleMenu:assignPermissions') || hasRole('system_admin')")
    public Result assignMenu(@RequestParam("roleId") Long roleId,
                             @RequestParam("menuIds") List<Long> menuIds) {
        return roleMenuService.assignPermissions(roleId, menuIds);
    }

    @GetMapping("/queryRoleHasMenu/{roleId}")
    public Result queryRoleHasMenu(@PathVariable("roleId") Long roleId) {
        return roleMenuService.queryRoleHasMenu(roleId);
    }
}
