package com.spms.service;

import com.spms.dto.Result;

import java.util.List;

public interface RoleMenuService {

    Result assignPermissions(Long roleId, List<Long> menuIds);

    Result queryRoleHasMenu(Long roleId);
}
