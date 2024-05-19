package com.spms.service;

import com.spms.dto.Result;
import com.spms.entity.ProjectResource;
import com.spms.entity.User;

import java.util.List;

public interface ProjectResourceService {
//    Result estimateCost(List<ProjectResource> projectCosts);
//
//    Result actualCost(List<ProjectResource> projectCosts);

    Result getMembersByProId(Long projectId,String userName);
}
