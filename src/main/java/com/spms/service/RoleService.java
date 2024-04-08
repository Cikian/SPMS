package com.spms.service;

import com.spms.dto.Result;
import com.spms.dto.RoleDTO;

public interface RoleService {
    Result list(RoleDTO roleDTO, Integer page, Integer size);
}
