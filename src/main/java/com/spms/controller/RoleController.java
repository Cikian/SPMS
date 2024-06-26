package com.spms.controller;

import com.spms.dto.Result;
import com.spms.dto.RoleDTO;
import com.spms.dto.UserDTO;
import com.spms.entity.Role;
import com.spms.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('sys:role:add') || hasRole('system_admin')")
    public Result add(@RequestBody Role role) {
        return roleService.add(role);
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sys:role:delete') || hasRole('system_admin')")
    public Result delete(@RequestBody Long[] ids) {
        return roleService.delete(ids);
    }

    @PostMapping("/updateStatus")
    @PreAuthorize("hasAuthority('sys:role:update:status') || hasRole('system_admin')")
    public Result updateStatus(@RequestBody Role role) {
        return roleService.updateStatus(role);
    }

    @PostMapping("/updateRoleInfo")
    @PreAuthorize("hasAuthority('sys:role:update:info') || hasRole('system_admin')")
    public Result updateRoleInfo(@RequestBody Role role) {
        return roleService.updateRoleInfo(role);
    }

    @GetMapping("/list")
    public Result list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return roleService.list(page, size);
    }

    @GetMapping("/queryById/{roleId}")
    public Result queryById(@PathVariable("roleId") Long roleId) {
        return roleService.queryById(roleId);
    }
}
