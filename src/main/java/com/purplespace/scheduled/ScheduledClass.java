package com.purplespace.scheduled;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author jiava
 * @Description DOING
 * @date 2022-12-11-17:51
 */
@Component
public class ScheduledClass {

    //@Scheduled(cron="0/5 * * * * ? ")   //每5秒执行一次
    public void execute(){
        System.out.println("欢迎访问 紫东空间 " + new Date());
    }

}
