package com.spms.entity.VO;

import lombok.Data;

/**
 * @Title: ProPeople
 * @Author Cikian
 * @Package com.spms.entity.VO
 * @Date 2024/4/10 上午12:54
 * @description: SPMS: 人力成本与项目关联
 */
@Data
public class ProPeople {
    private Long proId;
    private Long peoId;
    private int days;
}
