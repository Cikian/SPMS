package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.ProAnnouncement;
import com.spms.enums.ErrorCode;
import com.spms.service.ProAnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Title: ProAnnouncementController
 * @Author Cikian
 * @Package com.spms.controller
 * @Date 2024/5/7 上午12:14
 * @description: SPMS: 项目公告Controller
 */

@RestController
@RequestMapping("/proAnno")
public class ProAnnouncementController {
    @Autowired
    private ProAnnouncementService proAnnouncementService;

    @GetMapping("/byId")
    public Result getById(@RequestParam("proId") Long proId) {
        ProAnnouncement proAnnouncement = proAnnouncementService.selectById(proId);
        Integer code = proAnnouncement == null? ErrorCode.GET_FAIL : ErrorCode.GET_SUCCESS;
        String msg = proAnnouncement == null? "查询失败" : "查询成功";
        return new Result(code, msg, proAnnouncement);
    }

    @PostMapping
    public Result addAnno(@RequestBody ProAnnouncement proAnnouncement) {
        Boolean insert = proAnnouncementService.insert(proAnnouncement);
        Integer code = insert ? ErrorCode.ADD_SUCCESS : ErrorCode.ADD_FAIL;
        String msg = insert ? "发布成功" : "发布失败";

        return new Result(code, msg, null);
    }
}
