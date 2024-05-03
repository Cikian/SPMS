package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.Result;
import com.spms.entity.DictionaryType;
import com.spms.enums.ResultCode;
import com.spms.mapper.DictionaryTypeMapper;
import com.spms.security.LoginUser;
import com.spms.service.DictionaryTypeService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class DictionaryTypeServiceImpl extends ServiceImpl<DictionaryTypeMapper, DictionaryType> implements DictionaryTypeService {
    @Override
    public Result add(DictionaryType dictionaryType) {
        LambdaQueryWrapper<DictionaryType> dictionaryTypeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dictionaryTypeLambdaQueryWrapper.eq(DictionaryType::getDictionaryTypeName, dictionaryType.getDictionaryTypeName());
        if (this.getOne(dictionaryTypeLambdaQueryWrapper) != null) {
            return Result.fail(ResultCode.FAIL.getCode(), "字典类型已存在");
        }
        this.save(dictionaryType);
        return Result.success("添加成功");
    }

    @Override
    public Result list(DictionaryType dictionaryType, Integer page, Integer size) {
        Page<DictionaryType> dictionaryTypePage = new Page<>(page, size);

        LambdaQueryWrapper<DictionaryType> dictionaryTypeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dictionaryTypeLambdaQueryWrapper.eq(!Objects.isNull(dictionaryType.getDictionaryTypeName()), DictionaryType::getDictionaryTypeName, dictionaryType.getDictionaryTypeName());
        this.page(dictionaryTypePage, dictionaryTypeLambdaQueryWrapper);

        if (dictionaryTypePage.getRecords().isEmpty()) {
            return Result.fail(ResultCode.FAIL.getCode(), "暂无数据");
        }

        return Result.success(dictionaryTypePage);
    }
}
