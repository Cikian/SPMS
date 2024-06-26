package com.spms.controller;

import com.alibaba.druid.pool.DruidDataSource;
import com.spms.constants.SystemConstants;
import com.spms.dto.EmailVerifyDTO;
import com.spms.dto.PasswordUpdateDTO;
import com.spms.dto.Result;
import com.spms.dto.UserDTO;
import com.spms.entity.User;
import com.spms.mapper.RoleMapper;
import com.spms.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @SneakyThrows
    @PostMapping("/login")
    public Result login(@RequestBody User user, HttpServletRequest request) {
        request.setAttribute("userName",user.getUserName());
        return userService.login(user);
    }

    @GetMapping("/logout")
    public Result logout() {
        return userService.logout();
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('sys:user:add') || hasRole('system_admin')")
    public Result add(@RequestBody User user) {
        return userService.add(user);
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sys:user:delete') || hasRole('system_admin')")
    public Result delete(@RequestBody Long[] ids) {
        return userService.delete(ids);
    }

    @PostMapping("/list")
    public Result list(@RequestBody UserDTO userDTO,
                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return userService.list(userDTO, page, size);
    }

    @GetMapping("/queryById/{userId}")
    public Result queryById(@PathVariable("userId") Long userId) {
        return userService.queryById(userId);
    }

    @PostMapping("/updateStatus")
    @PreAuthorize("hasAuthority('sys:user:update:status') || hasRole('system_admin')")
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

    @PostMapping("/retrievePassword")
    public Result retrievePassword(@RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        return userService.retrievePassword(passwordUpdateDTO);
    }

    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        return userService.updatePassword(passwordUpdateDTO);
    }

    @PostMapping("/updateUserBaseInfo")
    public Result updateUserBaseInfo(@RequestBody User user) {
        return userService.updateUserBaseInfo(user);
    }

    @GetMapping("/queryCurrentUser")
    public Result queryCurrentUser() {
        return userService.queryCurrentUser();
    }

    @GetMapping("/queryCanAddToProject")
    public Result queryCanAddToProject() {
        return userService.queryCanAddToProject();
    }

    @GetMapping("/queryCanAddToProjectMember/{proId}")
    public Result queryCanAddToProjectMember(@PathVariable("proId") Long proId) {
        return userService.queryCanAddToProjectMember(proId);
    }

    @GetMapping("/queryProjectMembers/{projectId}/{type}")
    public Result queryProjectMembers(@PathVariable("projectId") Long projectId,
                                      @PathVariable("type") Integer type) {
        return userService.queryProjectMembers(projectId, type);
    }

}
