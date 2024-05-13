package com.spms.enums;

import lombok.Getter;

@Getter
public enum TestReportApprovalStatus {
    UNAUDITED(0, "未审核"),
    PASS(1, "通过"),
    NOT_PASS(2, "不通过");

    private final Integer code;
    private final String desc;

    TestReportApprovalStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
