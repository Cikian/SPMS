package com.spms.controller;

import com.spms.dto.Result;
import com.spms.service.RoleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roleUser")
public class RoleUserController {

    @Autowired
    private RoleUserService roleUserService;

    @PostMapping("/assignRole")
    @PreAuthorize("hasAuthority('sys:roleUser:assignRole') || hasRole('system_admin')")
    public Result assignRole(@RequestParam("userId") Long userId,
                             @RequestParam("roleIds") List<Long> roleIds) {
        return roleUserService.assignRole(userId, roleIds);
    }

    @GetMapping("/queryUserHasRole/{userId}")
    public Result queryUserHasRole(@PathVariable("userId") Long userId) {
        return roleUserService.queryUserHasRole(userId);
    }

}