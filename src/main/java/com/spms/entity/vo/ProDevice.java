package com.spms.entity.vo;

import lombok.Data;

/**
 * @Title: ProDevice
 * @Author Cikian
 * @Package com.spms.entity.VO
 * @Date 2024/4/10 上午12:56
 * @description: SPMS: 物力成本与项目关联
 */
@Data
public class ProDevice {
    private Long proId;
    private Long devId;
    private int days;
}
