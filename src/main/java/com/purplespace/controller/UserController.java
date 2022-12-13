package com.purplespace.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.purplespace.commoon.RedisTemp;
import com.purplespace.entity.User;
import com.purplespace.mapper.UserMapper;
import com.purplespace.service.UserService;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
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
    private RedisTemp redisTemp;
    @RequestMapping("/user")
    public String test(){
        List<User> user = redisTemp.getCacheList("userList");
        if (CollectionUtils.isEmpty(user)){
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            List<User> users = userMapper.selectList(queryWrapper);
            System.out.println("当前数据");
            users.forEach(System.out::println);
            redisTemp.setCacheList("userList",users);
        }
        List<User> userRes = redisTemp.getCacheList("userList");
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
