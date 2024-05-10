package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.dto.Result;
import com.spms.entity.Notification;
import com.spms.enums.ResultCode;
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
    public Boolean addNotification(Long receiverId, String title, String content) {
        Notification notification = new Notification();
        notification.setReceiverId(receiverId);
        notification.setTitle(title);
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

        List<Notification> notificationList = range.stream().map(notificationIdStr -> this.getById(Long.valueOf(notificationIdStr))).toList();

        return Result.success(notificationList);
    }

    @Override
    public Result getOldNotification() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();

        LambdaQueryWrapper<Notification> notificationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        notificationLambdaQueryWrapper.eq(Notification::getReceiverId, userId)
                .eq(Notification::getReadFlag, true);
        List<Notification> notificationList = this.list(notificationLambdaQueryWrapper);

        if (notificationList == null || notificationList.isEmpty()) {
            return Result.success("暂无历史通知");
        }
        return Result.success(notificationList);
    }

    @Override
    @Transactional
    public Result readNotification(Long notificationId) {
        if (notificationId == null) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getUserId();

        LambdaUpdateWrapper<Notification> notificationLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        notificationLambdaUpdateWrapper.eq(Notification::getNotificationId, notificationId)
                .set(Notification::getReadFlag, true);
        boolean update = this.update(notificationLambdaUpdateWrapper);

        if (!update) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }

        Long remove = redisTemplate.opsForList().remove(NOTIFICATION_KEY + userId, 1, notificationId.toString());
        if (remove == null || remove == 0) {
            return Result.fail(ResultCode.FAIL.getCode(), "参数错误");
        }
        return Result.success();
    }
}
