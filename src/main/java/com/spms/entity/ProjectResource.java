package com.spms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProjectResource {

    @TableId(type = IdType.ASSIGN_ID)
    private Long projectResourceId;

    private Long projectId;

    private Long resourceId;

    private Integer resourceType;

    private LocalDateTime estimateStartTime;

    private LocalDateTime estimateEndTime;

    private LocalDateTime actualStartTime;

    private LocalDateTime actualEndTime;

    private Integer usage;

    private BigDecimal estimateCost;

    private BigDecimal actualCost;
}
