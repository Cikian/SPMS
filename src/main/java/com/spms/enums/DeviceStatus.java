package com.spms.enums;

import lombok.Getter;

@Getter
public enum DeviceStatus {
    NORMAL(1786608633705680897L, "正常"),
    REPAIR(1786608663330050049L, "维修中"),
    SCRAP(1786608710385946625L, "已报废");

    private final Long code;
    private final String desc;

    DeviceStatus(Long code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}