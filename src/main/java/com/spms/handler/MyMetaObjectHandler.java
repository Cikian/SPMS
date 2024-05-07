package com.spms.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.spms.security.LoginUser;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

@Configuration
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();
        if (metaObject.hasGetter("createBy") && metaObject.hasSetter("createBy")) {
            metaObject.setValue("createBy", userId);
        }
        if (metaObject.hasGetter("createTime") && metaObject.hasSetter("createTime")) {
            metaObject.setValue("createTime", LocalDateTime.now());
        }
        if (metaObject.hasGetter("updateBy") && metaObject.hasSetter("updateBy")) {
            metaObject.setValue("updateBy", userId);
        }
        if (metaObject.hasGetter("updateTime") && metaObject.hasSetter("updateTime")) {
            metaObject.setValue("updateTime", LocalDateTime.now());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();
        metaObject.setValue("updateBy", userId);
        metaObject.setValue("updateTime", LocalDateTime.now());
    }

}
