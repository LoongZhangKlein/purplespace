package com.purplespace.controller;


import com.purplespace.cache.Redis;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author jiava
 * @Description DOING
 * @date 2022-12-09-11:08
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @Resource
    Redis redis;
    @GetMapping("/test")
    public String test(){
        System.out.println("ssssssssssss");
        System.out.println("ssssssssssss");
        redis.setKey("1","紫东");
        String key = (String) redis.getKey("1");
        System.out.println(key);

        return "设置成功";
    }
}
