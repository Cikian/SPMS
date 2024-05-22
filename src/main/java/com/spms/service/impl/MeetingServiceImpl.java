package com.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spms.entity.Meeting;
import com.spms.mapper.MeetingMapper;
import com.spms.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Title: MeetingServiceImpl
 * @Author Cikian
 * @Package com.spms.service.impl
 * @Date 2024/5/21 上午4:27
 * @description: SPMS:
 */

@Service
public class MeetingServiceImpl implements MeetingService {
    @Autowired
    private MeetingMapper meetingMapper;

    @Override
    public Boolean addMeeting(Meeting meeting) {
        return meetingMapper.insert(meeting) > 0;
    }

    @Override
    public List<Meeting> getByProId(Long proId) {
        LambdaQueryWrapper<Meeting> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Meeting::getProId, proId);
        return meetingMapper.selectList(lqw);
    }

    @Override
    public Boolean deleteById(Long meetId) {
        LambdaQueryWrapper<Meeting> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Meeting::getMeetId, meetId);
        return meetingMapper.delete(lqw) > 0;
    }
}
