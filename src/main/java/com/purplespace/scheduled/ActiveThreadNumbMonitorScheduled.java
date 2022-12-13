package com.purplespace.scheduled;

import com.purplespace.utils.ThreadPoolUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author loongzhang
 * @Description DOING
 * @date 2022-12-13-11:06
 */
@Component
@Slf4j
public class ActiveThreadNumbMonitorScheduled {
    @Resource
    ThreadPoolUtils threadPoolUtils;

    //每5秒执行一次
    @Scheduled(cron = "0/2 * * * * ? ")
    public void execute() {
        log.info("当前线程池存活线程数量{},当前系统时间:{}", threadPoolUtils.activeThreadNumb(),new Date());
    }
}
