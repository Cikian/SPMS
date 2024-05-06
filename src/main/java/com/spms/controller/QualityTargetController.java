package com.spms.controller;

import com.spms.service.QualityTargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qualityTarget")
public class QualityTargetController {

    @Autowired
    private QualityTargetService qualityTargetService;
}
