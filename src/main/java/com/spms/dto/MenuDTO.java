package com.spms.dto;

import lombok.Data;

@Data
public class MenuDTO {
    private Long menuId;
    private String menuName;
    private Boolean status;
    private Integer type;
    private String remark;
}
