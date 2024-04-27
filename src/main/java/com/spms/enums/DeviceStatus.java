package com.spms.enums;

import lombok.Getter;

@Getter
public enum DeviceStatus {
    NORMAL(0, "在用"),
    REPAIR(1, "维修中"),
    SCRAP(2, "已报废");

    private final Integer code;
    private final String desc;

    DeviceStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}