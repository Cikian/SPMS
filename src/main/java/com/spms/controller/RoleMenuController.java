package com.spms.controller;

import com.spms.dto.Result;
import com.spms.service.RoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roleMenu")
public class RoleMenuController {

    @Autowired
    private RoleMenuService roleMenuService;

    @PostMapping("/assignPermissions")
    @PreAuthorize("hasRole('system_admin')")
    public Result assignMenu(@RequestParam("roleId") Long roleId,
                             @RequestParam("menuIds") List<Long> menuIds) {
        return roleMenuService.assignPermissions(roleId, menuIds);
    }
}
