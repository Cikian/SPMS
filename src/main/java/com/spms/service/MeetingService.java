package com.spms.service;

import com.spms.entity.Meeting;

import java.util.List;

/**
 * @Title: MeetingService
 * @Author Cikian
 * @Package com.spms.service
 * @Date 2024/5/21 上午4:24
 * @description: SPMS: 会议
 */
public interface MeetingService {
    Boolean addMeeting(Meeting meeting);
    List<Meeting> getByProId(Long proId);
    Boolean deleteById(Long meetId);
}
