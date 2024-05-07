package com.spms.controller;

import com.spms.dto.Result;
import com.spms.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/getNotification")
    public Result getNotification() {
        return notificationService.getNotification();
    }

    @GetMapping("/getUnreadNotificationCount")
    public Result getUnreadNotificationCount(){
        return notificationService.getUnreadNotificationCount();
    }
}
