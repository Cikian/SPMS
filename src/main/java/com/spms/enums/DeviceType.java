package com.spms.enums;

import lombok.Getter;

@Getter
public enum DeviceType {
    SERVER(0, "服务器"),
    NETWORK(1, "网络设备"),
    STORAGE(2, "存储设备"),
    COMPUTE(3, "计算设备"),
    PERIPHERAL(4, "外围设备"),
    MOBILE(5, "移动设备");

    private final Integer code;
    private final String desc;

    DeviceType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static boolean contains(Integer type) {
        for (DeviceType deviceType : DeviceType.values()) {
            if (deviceType.getCode().equals(type)) {
                return true;
            }
        }
        return false;
    }
}
