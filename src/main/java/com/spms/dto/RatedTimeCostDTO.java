package com.spms.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RatedTimeCostDTO {
    private Long ratedTimeCostId;
    private String resourceName;
    private Integer resourceType;
    private BigDecimal dailyCost;
    private BigDecimal monthlyCost;
    private LocalDateTime updateTime;
}
