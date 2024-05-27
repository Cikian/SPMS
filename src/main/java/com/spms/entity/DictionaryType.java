package com.spms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;


@Data
public class DictionaryType {

    @TableId(type = IdType.ASSIGN_ID)
    private Long dictionaryTypeId;

    private String dictionaryTypeName;
}
