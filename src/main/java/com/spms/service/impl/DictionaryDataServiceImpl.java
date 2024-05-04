package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.Result;
import com.spms.entity.DictionaryData;
import com.spms.enums.ResultCode;
import com.spms.mapper.DictionaryDataMapper;
import com.spms.service.DictionaryDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.spms.constants.SystemConstants.DELETE;
import static com.spms.constants.SystemConstants.NOT_DELETE;

@Service
public class DictionaryDataServiceImpl extends ServiceImpl<DictionaryDataMapper, DictionaryData> implements DictionaryDataService {
    @Override
    public Result add(DictionaryData dictionaryData) {
        LambdaQueryWrapper<DictionaryData> dictionaryDataLabelLambdaQueryWrapper = new LambdaQueryWrapper<>();

        dictionaryDataLabelLambdaQueryWrapper.eq(DictionaryData::getDictionaryTypeId, dictionaryData.getDictionaryTypeId())
                .eq(DictionaryData::getLabel, dictionaryData.getLabel())
                .eq(DictionaryData::getDelFlag, NOT_DELETE);
        if (this.getOne(dictionaryDataLabelLambdaQueryWrapper) != null) {
            return Result.fail(ResultCode.FAIL.getCode(), "该字典分类下已存在该标签名");
        }

        LambdaQueryWrapper<DictionaryData> dictionaryDataValueLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dictionaryDataValueLambdaQueryWrapper.eq(DictionaryData::getDictionaryTypeId, dictionaryData.getDictionaryTypeId())
                .eq(DictionaryData::getValue, dictionaryData.getValue())
                .eq(DictionaryData::getDelFlag, NOT_DELETE);
        if (this.getOne(dictionaryDataValueLambdaQueryWrapper) != null) {
            return Result.fail(ResultCode.FAIL.getCode(), "该字典分类下已存在该值");
        }
        dictionaryData.setDelFlag(NOT_DELETE);
        this.save(dictionaryData);
        return Result.success("添加成功", dictionaryData);
    }

    @Override
    @Transactional
    public Result delete(Long id) {
        if (id == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }
        DictionaryData dictionaryData = this.getById(id);

        if (dictionaryData == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "该字典数据不存在");
        }

        dictionaryData.setDelFlag(DELETE);
        this.updateById(dictionaryData);

        return Result.success("删除成功");
    }

    @Override
    public Result queryByTypeId(Long dictionaryTypeId) {
        if (dictionaryTypeId == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        LambdaQueryWrapper<DictionaryData> dictionaryDataLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dictionaryDataLambdaQueryWrapper.eq(DictionaryData::getDictionaryTypeId, dictionaryTypeId)
                .select(DictionaryData::getDictionaryDataId, DictionaryData::getLabel, DictionaryData::getValue);
        List<DictionaryData> dictionaryDataList = this.list(dictionaryDataLambdaQueryWrapper);

        return Result.success(dictionaryDataList);
    }
}
