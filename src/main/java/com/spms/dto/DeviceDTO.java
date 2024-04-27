package com.spms.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DeviceDTO {
    private Long devId;
    private String devName;
    private Integer type;
    private LocalDateTime purchaseDate;
    private LocalDateTime warrantyExpiryDate;
    private BigDecimal cost;
    private Integer status;
}
