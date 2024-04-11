package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.Menu;
import com.spms.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('system_admin')")
    public Result allMenu() {
        return menuService.allMenu();
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('system_admin')")
    public Result addMenu(@RequestBody Menu menu) {
        return menuService.addMenu(menu);
    }
}
