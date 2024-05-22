package com.spms.listener;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import com.spms.SpmsApplication;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;

/**
 * @Title: CustomLogContextListener
 * @Author Cikian
 * @Package com.spms.listener
 * @Date 2024/5/22 上午3:52
 * @description: SPMS: 监听启动，获取启动路径
 */
public class CustomLogContextListener extends ContextAwareBase implements LoggerContextListener, LifeCycle {
    /** 存储日志路径标识 */
    public static final String LOG_PAHT_KEY = "LOG_PATH";

    @Override
    public void start() {
        ApplicationHome h = new ApplicationHome(getClass());
        File jarF = h.getSource();
        String dirPath = jarF.getParentFile().toString();

        // URL urls = SpmsApplication.class.getProtectionDomain().getCodeSource().getLocation();
        URL urls = SpmsApplication.class.getProtectionDomain().getCodeSource().getLocation();
        // String filePath=null;
        String filePath=dirPath;
        // try{
        //     filePath= URLDecoder.decode(urls.getPath(),"utf-8");//转化为utf-8编码
        //     System.out.println("CustomLogContextListener_start_filePath:"+filePath);
        // }catch(Exception e){
        //     e.printStackTrace();
        // }
        System.setProperty(LOG_PAHT_KEY, filePath);
        Context context = getContext();
        context.putProperty(LOG_PAHT_KEY,  filePath);
        System.out.println("CustomLogContextListener_start_LOG_PATH:"+filePath);
    }

    @Override
    public boolean isResetResistant() {
        return false;
    }

    @Override
    public void onStart(LoggerContext context) {

    }

    @Override
    public void onReset(LoggerContext context) {

    }

    @Override
    public void onStop(LoggerContext context) {

    }

    @Override
    public void onLevelChange(Logger logger, Level level) {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isStarted() {
        return false;
    }
}
