package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.DictionaryType;
import com.spms.service.DictionaryTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dictionaryType")
public class DictionaryTypeController {

    @Autowired
    private DictionaryTypeService dictionaryTypeService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('system_admin')")
    public Result add(@RequestBody DictionaryType dictionaryType) {
        return dictionaryTypeService.add(dictionaryType);
    }

    @PostMapping("/list")
    public Result list(@RequestBody DictionaryType dictionaryType,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer size) {
        return dictionaryTypeService.list(dictionaryType, page, size);
    }

    @GetMapping("/queryById/{dictionaryTypeId}")
    public Result queryById(@PathVariable("dictionaryTypeId") Long dictionaryTypeId) {
        return dictionaryTypeService.queryById(dictionaryTypeId);
    }

}
