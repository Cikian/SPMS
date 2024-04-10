package com.spms.service;

import com.spms.dto.Result;
import com.spms.entity.Role;

public interface RoleService {
    Result list(Integer page, Integer size);

    Result add(Role role);

    Result delete(Long[] ids);

    Result queryById(Long roleId);

    Result queryUserListByRoleId(Long roleId);
}
