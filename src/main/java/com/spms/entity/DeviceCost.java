package com.spms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeviceCost {

    @TableId
    private Long devId;

    private BigDecimal dailyRate;

    private BigDecimal monthlyRate;
}
