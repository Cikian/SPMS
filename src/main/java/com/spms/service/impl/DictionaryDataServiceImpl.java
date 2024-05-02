package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.Result;
import com.spms.entity.DictionaryData;
import com.spms.enums.ResultCode;
import com.spms.mapper.DictionaryDataMapper;
import com.spms.service.DictionaryDataService;
import org.springframework.stereotype.Service;

@Service
public class DictionaryDataServiceImpl extends ServiceImpl<DictionaryDataMapper, DictionaryData> implements DictionaryDataService {
    @Override
    public Result add(DictionaryData dictionaryData) {
        LambdaQueryWrapper<DictionaryData> dictionaryDataLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dictionaryDataLambdaQueryWrapper.eq(DictionaryData::getDictionaryDataId, dictionaryData.getDictionaryTypeId())
                .eq(DictionaryData::getLabel, dictionaryData.getLabel());
        if (this.getOne(dictionaryDataLambdaQueryWrapper) != null) {
            return Result.fail(ResultCode.FAIL.getCode(), "字典数据已存在");
        }
        this.save(dictionaryData);
        return Result.success("添加成功");
    }
}
