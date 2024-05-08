package com.spms.enums;

import lombok.Getter;

@Getter
public enum DeviceUsage {
    FREE(1786608794511101953L, "空闲"),
    OCCUPIED(1786609025629835266L, "被占用");

    private final Long code;
    private final String desc;

    DeviceUsage(Long code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
