package com.spms.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.spms.dto.Result;
import com.spms.entity.User;
import com.spms.service.UserService;
import com.spms.service.impl.UserServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Title: UserListener
 * @Author Cikian
 * @Package com.spms.listener
 * @Date 2024/6/3 上午8:57
 * @description: SPMS: 导入用户监听器
 */

//使用EasyExcel进行学生信息的数据导入
public class UserListener extends AnalysisEventListener<User> {

    // private static final int BATCH_COUNT = 10;
    private int successCount = 0;
    private int failureCount = 0;

    private UserService userService;
    public UserListener(){
        userService = new UserServiceImpl();
    }

    public UserListener(UserService userService){
        this.userService = userService;
    }

    //读取数据的时候会执行invoke方法
    @Override
    public void invoke(User user, AnalysisContext analysisContext) {
        Result add = userService.add(user);
        if (add.getCode() == 200){
            successCount++;
        } else {
            failureCount++;
        }
    }

    //所有的数据解析完成之后都会来调用
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // this.userService.saveListStu(list); //确保最后遗留的数据保存在数据库中
    }

    //获取成功导入的数量
    public Map getCount() {
        Map<String, Integer> result = new HashMap<>();
        result.put("success", successCount);
        result.put("fail", failureCount);
        return result;
    }


}
