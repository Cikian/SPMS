package com.spms.controller;

import com.spms.dto.Result;
import com.spms.service.RecentVisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recentVisit")
public class RecentVisitController {

    @Autowired
    private RecentVisitService recentVisitService;

    @PostMapping("/recordVisit/{id}/{type}")
    public Result recordVisit(@PathVariable("id") Long id, @PathVariable("type") Integer type) {
        return recentVisitService.recordVisit(id, type);
    }

    @GetMapping("getRecentVisits")
    public Result getRecentVisits() {
        return recentVisitService.getRecentVisits();
    }
}
