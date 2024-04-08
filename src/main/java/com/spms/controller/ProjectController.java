package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.Project;
import com.spms.enums.ResultCode;
import com.spms.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Result addProject(@RequestBody Project project){
        if (proService.addPro(project)){
            return Result.success("添加成功");
        }
        return Result.fail(ResultCode.FAIL.getCode(), "添加失败");
    }
}
