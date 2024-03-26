package com.spms.service;

import com.spms.dto.Result;
import com.spms.entity.User;

public interface UserService {

    Result login(User user);
}
