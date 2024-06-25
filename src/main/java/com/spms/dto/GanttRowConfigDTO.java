package com.spms.dto;

import lombok.Data;

/**
 * @Title: GanttRowConfigDTO
 * @Author Cikian
 * @Package com.spms.dto
 * @Date 2024/6/11 下午7:39
 * @description: SPMS: 甘特图行配置
 */
@Data
public class GanttRowConfigDTO {
    private String id;
    private String label;
    private boolean hasHandles;
    private boolean immobile;
    private GanttRowStyleDTO style;
}
