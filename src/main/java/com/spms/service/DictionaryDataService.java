package com.spms.service;

import com.spms.dto.Result;
import com.spms.entity.DictionaryData;

public interface DictionaryDataService {
    Result add(DictionaryData dictionaryData);

    Result delete(Long id);

    Result queryByTypeId(Long dictionaryTypeId);
}
