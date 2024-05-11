package com.spms.service;

import com.spms.dto.Result;
import com.spms.entity.Project;
import com.spms.dto.AddProjectDTO;

import java.util.List;

/**
 * @Title: ProjectService
 * @Author Cikian
 * @Package com.spms.service
 * @Date 2024/4/8 下午1:50
 * @description: SPMS: 项目业务层接口
 */


public interface ProjectService {
    boolean addPro(AddProjectDTO addProjectDTO);

    List<Project> getAllPro();

    List<Project> getProjectByStatus(Integer status);

    Project getProById(Long id);
}
