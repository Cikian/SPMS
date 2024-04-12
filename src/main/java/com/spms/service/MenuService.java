package com.spms.service;

import com.spms.dto.Result;
import com.spms.entity.Menu;

public interface MenuService {
    Result allMenu();

    Result addMenu(Menu menu);
}
