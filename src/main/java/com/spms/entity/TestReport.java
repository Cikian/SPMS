package com.spms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class TestReport {

    @TableId(type = IdType.ASSIGN_ID)
    private Long testReportId;

    private Long testPlanId;

    private String reportFile;

    private Integer approvalStatus;
}
