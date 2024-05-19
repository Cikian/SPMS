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

    List<Project> getNeedCompletePro();

    List<Project> myPro();

    List<Project> mySubmit();

    List<Project> getAudit();

    List<Project> searchPro(String keyword);

    List<Project> getProjectByStatus(Integer status);

    ProjectDTO getProById(Long id);

    Result addMember(AddProPeopleDTO addProPeopleDTO);

    Result deleteMember(DeleteProPeopleDTO deleteProPeopleDTO);

    Boolean changeStatus(Long id, Integer status);

    Boolean deleteByProId(Long id);
}
