package com.spms.enums;

import lombok.Getter;

@Getter
public enum DeviceUsage {
    FREE(0, "空闲"),
    OCCUPIED(1, "被占用");

    private final Integer code;
    private final String desc;

    DeviceUsage(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
