package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.User;
import com.spms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        return userService.login(user);
    }

    @GetMapping("/logout")
    public Result logout(){
        return userService.logout();
    }
}
