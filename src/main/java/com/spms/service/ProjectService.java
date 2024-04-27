package com.spms.service;

import com.spms.entity.Project;
import com.spms.entity.vo.ProjectVo;

import java.util.List;

/**
 * @Title: ProjectService
 * @Author Cikian
 * @Package com.spms.service
 * @Date 2024/4/8 下午1:50
 * @description: SPMS: 项目业务层接口
 */


public interface ProjectService {
    boolean addPro(ProjectVo projectVo);
    List<Project> getAllPro();
}
