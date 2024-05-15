package com.spms.dto;

import lombok.Data;

@Data
public class RecentVisitDTO {
    private Long id;

    private Integer type;

    private Integer demandType;

    private String name;

    private String flag;
}
