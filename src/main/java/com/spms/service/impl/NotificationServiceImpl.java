package com.spms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.Result;
import com.spms.entity.Notification;
import com.spms.mapper.NotificationMapper;
import com.spms.security.LoginUser;
import com.spms.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.spms.constants.RedisConstants.NOTIFICATION_KEY;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    @Transactional
    public Boolean addNotification(Long receiverId, String content) {
        Notification notification = new Notification();
        notification.setReceiverId(receiverId);
        notification.setContent(content);
        notification.setReadFlag(false);

        boolean isSuccess = this.save(notification);
        if (!isSuccess) {
            return false;
        }
        redisTemplate.opsForList().leftPush(NOTIFICATION_KEY + receiverId, notification.getNotificationId().toString());
        return true;
    }

    @Override
    public Result getUnreadNotificationCount() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();
        Long unreadCount = redisTemplate.opsForList().size(NOTIFICATION_KEY + userId);
        return Result.success(unreadCount);
    }

    @Override
    public Result getNotification() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();

        List<String> range = redisTemplate.opsForList().range(NOTIFICATION_KEY + userId, 0, -1);
        if (range == null || range.isEmpty()) {
            return Result.success("暂无最新通知");
        }

        List<Notification> notificationList = range.stream().map(notificationIdStr -> {
            Long notificationId = Long.valueOf(notificationIdStr);
            Notification notification = this.getById(notificationId);
            notification.setReadFlag(true);
            this.updateById(notification);
            return notification;
        }).toList();

        redisTemplate.delete(NOTIFICATION_KEY + userId);
        return Result.success(notificationList);
    }
}
