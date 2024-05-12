package com.spms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class TestCase {
    @TableId(type = IdType.ASSIGN_ID)
    private Long testCaseId;

    private Long testPlanId;

    private Integer priority;

    private String caseName;

    private String caseContent;

    private Boolean status;

    private Boolean delFlag;
}
