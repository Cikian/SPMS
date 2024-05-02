package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.DictionaryType;
import com.spms.service.DictionaryTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dictionaryType")
public class DictionaryTypeController {

    @Autowired
    private DictionaryTypeService dictionaryTypeService;

    @PostMapping("/add")
    public Result add(@RequestBody DictionaryType dictionaryType){
        return dictionaryTypeService.add(dictionaryType);
    }

}
