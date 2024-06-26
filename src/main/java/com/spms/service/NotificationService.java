package com.spms.service;

import com.spms.dto.Result;

public interface NotificationService {

    Boolean addNotification(Long receiverId, String title, String content);

    Result getUnreadNotificationCount();

    Result getNotification();

    Result getOldNotification();

    Result readNotification(Long notificationId);
}
