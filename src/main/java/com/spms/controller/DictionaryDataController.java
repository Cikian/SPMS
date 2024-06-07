package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.DictionaryData;
import com.spms.service.DictionaryDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dictionaryData")
public class DictionaryDataController {

    @Autowired
    private DictionaryDataService dictionaryDataService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('dictionaryData:add') || hasRole('system_admin')")
    public Result add(@RequestBody DictionaryData dictionaryData) {
        return dictionaryDataService.add(dictionaryData);
    }

    @GetMapping("/delete/{dictionaryDataId}")
    @PreAuthorize("hasAuthority('dictionaryData:delete') || hasRole('system_admin')")
    public Result delete(@PathVariable("dictionaryDataId") Long dictionaryDataId) {
        return dictionaryDataService.delete(dictionaryDataId);
    }

    @GetMapping("/queryByTypeId/{dictionaryTypeId}")
    public Result queryByTypeId(@PathVariable("dictionaryTypeId") Long dictionaryTypeId) {
        return dictionaryDataService.queryByTypeId(dictionaryTypeId);
    }
}
