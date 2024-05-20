package com.spms.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Title: ProPeople
 * @Author Cikian
 * @Package com.spms.entity.VO
 * @Date 2024/4/10 上午12:54
 * @description: SPMS: 人力成本与项目关联
 */
@Data
public class DeleteProResourceDTO {
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime actualStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime actualEndTime;
}
