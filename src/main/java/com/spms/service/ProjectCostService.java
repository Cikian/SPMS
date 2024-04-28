package com.spms.service;

import com.spms.dto.Result;
import com.spms.entity.ProjectCost;

import java.util.List;

public interface ProjectCostService {
    Result estimateCost(List<ProjectCost> projectCosts);

    Result actualCost(List<ProjectCost> projectCosts);
}
