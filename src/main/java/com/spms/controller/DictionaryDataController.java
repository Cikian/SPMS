package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.DictionaryData;
import com.spms.service.DictionaryDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dictionaryData")
public class DictionaryDataController {

    @Autowired
    private DictionaryDataService dictionaryDataService;

    @PostMapping("/add")
    public Result add(@RequestBody DictionaryData dictionaryData){
        return dictionaryDataService.add(dictionaryData);
    }
}
