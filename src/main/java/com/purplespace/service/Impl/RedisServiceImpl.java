package com.purplespace.service.Impl;

import com.purplespace.commoon.RedisTemp;
import com.purplespace.config.RedisConfig;
import com.purplespace.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author loongzhang
 * @Description DOING
 * @date 2022-12-12-14:20
 */
@Service
@Slf4j
public class RedisServiceImpl implements RedisService {
    @Resource
    RedisTemp redisTemp;
    @Override
    public void operationTest() {

    }

    @Override
    public void set() {
        int times=10000;
        long start = System.currentTimeMillis();
        redisTemp.setObjectKey("loong","1",60);
        String loong = String.valueOf(redisTemp.getObjectKey("loong"));
        log.info("loong{}",loong);
       log.info("当前开始时间{}",start);
        for (int i = 0; i < times; i++) {
            String name="loong"+i;
            redisTemp.setObjectKey(name,i,60);
        }
        log.info("未使用管道执行消耗时间{}", System.currentTimeMillis()-start);

        long startS = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            String name="loong"+i;
            redisTemp.executeStringPipelined(name,i,60);
        }
        log.info("使用管道执行消耗时间{}", System.currentTimeMillis()-startS);
    }
}
