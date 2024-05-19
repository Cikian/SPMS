package com.spms.service;

import com.spms.dto.*;
import com.spms.entity.Project;

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

    ProjectDTO getProById(Long id);

    Result addMember(AddProPeopleDTO addProPeopleDTO);

    Result deleteMember(DeleteProPeopleDTO deleteProPeopleDTO);
}
