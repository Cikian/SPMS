package com.spms.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DeviceDTO {
    private Long devId;
    private String devName;
    private Long typeId;
    private String typeName;
    private LocalDateTime purchaseDate;
    private LocalDateTime warrantyExpiryDate;
    private BigDecimal purchaseCost;
    private Long status;
    private Long deviceUsage;
}
