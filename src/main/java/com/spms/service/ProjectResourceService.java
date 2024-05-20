package com.spms.service;

import com.spms.dto.Result;
import com.spms.entity.ProjectResource;
import com.spms.entity.User;

import java.util.List;

public interface ProjectResourceService {
    Result getMembersByProId(Long projectId,String userName);

    Result getDevicesByProId(Long proId, String deviceName);
}
