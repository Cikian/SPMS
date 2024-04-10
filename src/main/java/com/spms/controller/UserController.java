package com.spms.controller;

import com.spms.constants.SystemConstants;
import com.spms.dto.EmailVerifyDTO;
import com.spms.dto.PasswordUpdateDTO;
import com.spms.dto.Result;
import com.spms.dto.UserDTO;
import com.spms.entity.User;
import com.spms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Result logout() {
        return userService.logout();
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('system_admin')")
    public Result add(@RequestBody User user) {
        return userService.add(user);
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('system_admin')")
    public Result delete(@RequestBody Long[] ids) {
        return userService.delete(ids);
    }

    @PostMapping("/list")
    @PreAuthorize("hasRole('system_admin')")
    public Result list(@RequestBody UserDTO userDTO,
                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return userService.list(userDTO, page, size);
    }

    @GetMapping("/queryById/{userId}")
    @PreAuthorize("hasRole('system_admin')")
    public Result queryById(@PathVariable("userId") Long userId) {
        return userService.queryById(userId);
    }

    @GetMapping("/updateStatus")
    @PreAuthorize("hasRole('system_admin')")
    public Result updateStatus(@RequestBody UserDTO userDTO) {
        return userService.updateStatus(userDTO);
    }

    @PostMapping("/sendEmailCode")
    public Result sendEmailCode(@RequestBody User user) {
        return userService.sendEmailCode(user.getEmail());
    }

    @PostMapping("/verifyEmail")
    public Result verifyEmail(@RequestBody EmailVerifyDTO emailVerifyDTO) {
        return userService.verifyEmail(emailVerifyDTO);
    }

    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        return userService.updatePassword(passwordUpdateDTO);
    }


}
