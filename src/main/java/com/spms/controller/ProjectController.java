package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.Project;
import com.spms.entity.VO.ProjectVo;
import com.spms.enums.ResultCode;
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
    public Result addProject(@RequestBody ProjectVo projectVo){
        System.out.println(projectVo);
        if (proService.addPro(projectVo)){
            return Result.success("添加成功");
        }
        return Result.fail(ResultCode.FAIL.getCode(), "添加失败");
    }

    @GetMapping
    public Result getAllProject(){
        return Result.success(proService.getAllPro());
    }
}
