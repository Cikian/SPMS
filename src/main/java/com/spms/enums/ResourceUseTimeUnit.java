package com.spms.enums;

import lombok.Getter;

@Getter
public enum ResourceUseTimeUnit {
    DAY(1, "天"),
    MONTH(2, "月");

    private final Integer code;
    private final String name;

    ResourceUseTimeUnit(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
