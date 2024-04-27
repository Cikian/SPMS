package com.spms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserCost {

    @TableId
    private Long userId;

    private BigDecimal dailyRate;

    private BigDecimal monthlyRate;
}
