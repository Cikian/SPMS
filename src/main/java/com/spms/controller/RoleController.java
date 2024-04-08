package com.spms.controller;

import com.spms.dto.Result;
import com.spms.dto.RoleDTO;
import com.spms.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('admin')")
    public Result list(@RequestBody RoleDTO roleDTO,
                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return roleService.list(roleDTO, page, size);
    }
}
