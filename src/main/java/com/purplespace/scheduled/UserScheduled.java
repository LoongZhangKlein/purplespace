package com.purplespace.scheduled;

import com.purplespace.service.Impl.UserServiceImpl;
import com.purplespace.service.UserService;
import com.purplespace.utils.ThreadPoolUtils;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author loongzhang
 * @Description DOING
 * @date 2022-12-13-16:07
 */
@Component
@Slf4j
public class UserScheduled {
    @Resource
    ThreadPoolUtils threadPoolUtils;
    @Resource
    UserServiceImpl userServiceImpl;

    //每5秒执行一次
    //@Scheduled(cron = "0/2 * * * * ? ")
    public void execute() {
//        log.info("当前线程池存活线程数量{},当前系统时间:{}", threadPoolUtils.activeThreadNumb(),df.format(new Date()));
        log.info("用户数据数量{}", userServiceImpl.getListSize());
    }
}
