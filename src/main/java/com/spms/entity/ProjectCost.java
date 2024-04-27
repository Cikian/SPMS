package com.spms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProjectCost {

    @TableId(type = IdType.ASSIGN_ID)
    private Long proCostId;

    private Long projectId;

    private Long resourceId;

    private Integer estimateUseTime;

    private Integer actualUseTime;

    private Integer timeUnit;

    private BigDecimal estimateCost;

    private BigDecimal actualCost;
}
