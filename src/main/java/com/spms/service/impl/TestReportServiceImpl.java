package com.spms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spms.entity.TestReport;
import com.spms.mapper.TestReportMapper;
import com.spms.service.TestReportService;
import org.springframework.stereotype.Service;

@Service
public class TestReportServiceImpl extends ServiceImpl<TestReportMapper, TestReport> implements TestReportService {
}
