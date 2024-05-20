package com.spms.controller;

import com.spms.dto.Result;
import com.spms.entity.Meeting;
import com.spms.enums.ErrorCode;
import com.spms.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Title: MeetingController
 * @Author Cikian
 * @Package com.spms.controller
 * @Date 2024/5/21 上午4:27
 * @description: SPMS: 会议
 */

@RestController
@RequestMapping("/meeting")
public class MeetingController {
    @Autowired
    private MeetingService meetingService;


    @GetMapping
    public Result getMeetingListByProId(@RequestParam("proId") Long proId) {
        List<Meeting> byProId = meetingService.getByProId(proId);
        Integer code = byProId.isEmpty()? ErrorCode.GET_FAIL : ErrorCode.GET_SUCCESS;
        String msg = byProId.isEmpty()? "无数据" : "获取成功 ";
        return new Result(code, msg, byProId);
    }
}
