package com.spms.enums;

import lombok.Getter;

@Getter
public enum ReviewStatus {
    PENDING(0, "待审核"),
    PASS(1, "通过"),
    REJECT(2, "未通过");

    private final Integer code;
    private final String desc;

    ReviewStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
