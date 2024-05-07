package com.spms.enums;

import lombok.Getter;

@Getter
public enum ProjectMemberType {
    ALL(0, "所有人"),
    CREATOR(1, "创建者"),
    MANAGER(2, "管理者"),
    DEVELOPER(3, "开发者"),
    TESTER(4, "测试者"),
    OBSERVER(5, "观察者");

    private final Integer code;
    private final String desc;

    ProjectMemberType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
