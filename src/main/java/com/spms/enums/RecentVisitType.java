package com.spms.enums;

import lombok.Getter;

@Getter
public enum RecentVisitType {
    PROJECT(1, "项目"),
    DEMAND(2, "需求"),
    TEST_PLAN(3, "测试计划");

    private final Integer type;
    private final String desc;

    RecentVisitType(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
