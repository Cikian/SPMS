package com.spms.dto;

import lombok.Data;

/**
 * @Title: GanttRowDTO
 * @Author Cikian
 * @Package com.spms.dto
 * @Date 2024/6/11 下午7:16
 * @description: SPMS: 甘特图行
 */
@Data
public class GanttRowDTO {
    private String myBeginDate;
    private String myEndDate;
    private GanttRowConfigDTO ganttBarConfig;
}
