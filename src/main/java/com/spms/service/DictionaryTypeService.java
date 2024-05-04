package com.spms.service;

import com.spms.dto.Result;
import com.spms.entity.DictionaryType;

public interface DictionaryTypeService{
    Result add(DictionaryType dictionaryType);

    Result list(DictionaryType dictionaryType, Integer page, Integer size);

    Result queryById(Long dictionaryTypeId);
}
