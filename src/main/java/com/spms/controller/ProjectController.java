package com.spms.controller;

import com.spms.dto.AddProjectDTO;
import com.spms.dto.ProjectDTO;
import com.spms.dto.Result;
import com.spms.enums.ErrorCode;
import com.spms.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.spms.dto.*;

/**
 * @Title: ProjectController
 * @Author Cikian
 * @Package com.spms.controller
 * @Date 2024/4/9 上午12:51
 * @description: SPMS: 项目控制器类
 */
@RestController
@RequestMapping("/pro")
public class ProjectController {
    @Autowired
    private ProjectService proService;

    @PostMapping
    public Result addProject(@RequestBody AddProjectDTO addProjectDTO) {
        System.out.println(addProjectDTO);
        boolean b = proService.addPro(addProjectDTO);
        Integer code = b ? ErrorCode.ADD_SUCCESS : ErrorCode.ADD_FAIL;
        String msg = b ? "添加成功" : "添加失败";

        return new Result(code, msg, null);
    }

    @GetMapping()
    public Result getAllProject() {
        return Result.success(proService.getAllPro());
    }

    @GetMapping("/getProjectByStatus/{status}")
    public Result getProjectByStatus(@PathVariable("status") Integer status) {
        return Result.success(proService.getProjectByStatus(status));
    }

    @GetMapping("/getByProId/{proId}")
    public Result getProById(@PathVariable("proId") Long proId) {
        ProjectDTO proById = proService.getProById(proId);
        Integer code = proById != null ? ErrorCode.GET_SUCCESS : ErrorCode.GET_FAIL;
        String msg = proById != null ? "获取成功" : "获取失败";
        return new Result(code, msg, proById);
    }

    @PostMapping("/addMember")
    public Result addMember(@RequestBody AddProPeopleDTO addProPeopleDTO) {
        return proService.addMember(addProPeopleDTO);
    }

    @PostMapping("/deleteMember")
    public Result deleteMember(@RequestBody DeleteProPeopleDTO deleteProPeopleDTO) {
        return proService.deleteMember(deleteProPeopleDTO);
    }

    @PutMapping("/changeStatus/{proId}/{status}")
    public Result changeStatus(@PathVariable("proId") Long proId, @PathVariable("status") Integer status) {
        boolean b = proService.changeStatus(proId, status);
        Integer code = b ? ErrorCode.UPDATE_SUCCESS : ErrorCode.UPDATE_FAIL;
        String msg = b ? "更新成功" : "更新失败";
        return new Result(code, msg, null);
    }
}
