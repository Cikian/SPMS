package com.spms.enums;

import lombok.Getter;

@Getter
public enum ResourceType {
    EMPLOYEE(1, "员工"),
    DEVICE(2, "设备");

    private final Integer code;
    private final String name;

    ResourceType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
