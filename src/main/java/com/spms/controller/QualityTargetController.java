package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.QualityTarget;
import com.spms.service.QualityTargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/qualityTarget")
public class QualityTargetController {

    @Autowired
    private QualityTargetService qualityTargetService;

    @PostMapping("/add")
    public Result add(@RequestBody QualityTarget qualityTarget) {
        return qualityTargetService.add(qualityTarget);
    }

    @GetMapping("/list")
    public Result list(@RequestBody QualityTarget qualityTarget,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer size) {
        return qualityTargetService.list(qualityTarget,page,size);
    }

    @PostMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        return qualityTargetService.delete(ids);
    }
}
