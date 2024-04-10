package com.spms.service;

import com.spms.dto.Result;
import com.spms.dto.RoleDTO;
import com.spms.entity.Role;

public interface RoleService {
    Result list(RoleDTO roleDTO, Integer page, Integer size);

    Result add(Role role);

    Result delete(Long[] ids);
}
