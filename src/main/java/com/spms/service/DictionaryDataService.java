package com.spms.service;

import com.spms.dto.Result;
import com.spms.entity.DictionaryData;

public interface DictionaryDataService {
    Result add(DictionaryData dictionaryData);

    Result list(Long dictionaryTypeId);

    Result delete(Long id);
}
