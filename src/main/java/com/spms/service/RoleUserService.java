package com.spms.service;

import com.spms.dto.Result;

import java.util.List;

public interface RoleUserService {
    Result assignRole(Long userId, List<Long> roleIds);

    Result queryUserHasRole(Long userId);
}
