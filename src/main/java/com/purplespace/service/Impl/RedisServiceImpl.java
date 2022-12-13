package com.purplespace.service.Impl;

import com.purplespace.config.RedisConfig;
import com.purplespace.service.RedisService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author loongzhang
 * @Description DOING
 * @date 2022-12-12-14:20
 */
@Service
public class RedisServiceImpl implements RedisService {
    @Resource
    RedisConfig redisConfig;
    @Override
    public void operationTest() {

    }
}
