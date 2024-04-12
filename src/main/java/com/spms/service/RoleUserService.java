package com.spms.service;

import com.spms.dto.Result;

import java.util.List;

public interface RoleUserService {

    Result delete(Long roleId);

    Result assignRole(Long userId, List<Long> roleIds);

    Result queryUserHasRole(Long userId);

    Result queryUserListByRoleId(Long roleId);
}
