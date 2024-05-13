package com.spms.enums;

import lombok.Getter;

@Getter
public enum TestPlanStatus {
//    0全部 1未开始 2进行中 3已完成
    ALL(0, "全部"),
    NOT_STARTED(1, "未开始"),
    IN_PROGRESS(2, "进行中"),
    COMPLETED(3, "已完成");

    private final Integer code;
    private final String desc;

    TestPlanStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
