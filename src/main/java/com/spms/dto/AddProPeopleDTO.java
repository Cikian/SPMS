package com.spms.dto;

import lombok.Data;

/**
 * @Title: ProPeople
 * @Author Cikian
 * @Package com.spms.entity.VO
 * @Date 2024/4/10 上午12:54
 * @description: SPMS: 人力成本与项目关联
 */
@Data
public class AddProPeopleDTO {
    private Long proId;
    private Long peoId;
    private int days;
}
