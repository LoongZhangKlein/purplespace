package com.purplespace.controller;

import com.purplespace.service.RedisService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author loongzhang
 * @Description DOING
 * @date 2022-12-13-14:31
 */
@RestController
@RequestMapping("/redis")
public class RedisController {
    @Resource
    RedisService redisService;

    @RequestMapping("/testSet")
    public void setTest() {
        redisService.set();
    }
}
