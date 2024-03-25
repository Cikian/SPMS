package com.spms.service;

import com.spms.common.Result;
import com.spms.entity.User;

public interface UserService {

    Result login(User user);
}
