package com.purplespace.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.purplespace.commoon.RedisService;
import com.purplespace.config.RedisConfig;
import com.purplespace.entity.User;
import com.purplespace.mapper.UserMapper;
import com.purplespace.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * @author loongzhang
 * @Description DOING
 * @date 2022-12-12-15:28
 */
@RequestMapping("/user")
@RestController
public class UserController {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;
    @Resource
    private RedisService redisService;
    @RequestMapping("/user")
    public String test(){
        List<User> user = redisService.getCacheList("userList");
        if (CollectionUtils.isEmpty(user)){
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            List<User> users = userMapper.selectList(queryWrapper);
            System.out.println("当前数据");
            users.forEach(System.out::println);
            redisService.setCacheList("userList",users);
        }
        List<User> userRes = redisService.getCacheList("userList");
        userRes.forEach(System.out::println);
        return "";
    }
    @RequestMapping("/add")
    public int add() throws ExecutionException, InterruptedException {
        int userCustomer = userService.add();
        System.out.println(userCustomer);
        return 1;


    }

}
