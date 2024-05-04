package com.spms.controller;

import com.spms.dto.Result;
import com.spms.dto.AddProjectDTO;
import com.spms.enums.ErrorCode;
import com.spms.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public Result getAllProject() {
        return Result.success(proService.getAllPro());
    }
}
