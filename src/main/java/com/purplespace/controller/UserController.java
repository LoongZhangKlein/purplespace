package com.purplespace.controller;

import com.alibaba.druid.wall.Violation;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.purplespace.commoon.RedisTemp;
import com.purplespace.entity.User;
import com.purplespace.mapper.UserMapper;
import com.purplespace.service.Impl.UserServiceImpl;
import com.purplespace.service.UserService;
import com.purplespace.utils.ThreadPoolUtils;
import lombok.extern.slf4j.Slf4j;
import netscape.javascript.JSObject;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author loongzhang
 * @Description DOING
 * @date 2022-12-12-15:28
 */
@RequestMapping("/user")
@RestController
@Slf4j
public class UserController {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;
    @Resource
    private UserServiceImpl userServiceImpl;
    @Resource
    private RedisTemp redisTemp;
    private int runTimes=0;
    @RequestMapping("/user")
    public String test() {
        List<User> user = redisTemp.getCacheList("userList");
        if (CollectionUtils.isEmpty(user)) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            List<User> users = userMapper.selectList(queryWrapper);
            System.out.println("当前数据");
            users.forEach(System.out::println);
            redisTemp.setCacheList("userList", users);
        }
        List<User> userRes = redisTemp.getCacheList("userList");
        userRes.forEach(System.out::println);
        return "";
    }

    @RequestMapping("/add")
    public int add() {
        int userCustomer = userService.add();
        System.out.println(userCustomer);
        return 1;
    }

    @RequestMapping("/clear")
    public int clear() {
        UserServiceImpl.userList.clear();
        System.out.println("清除成功");
        return 1;
    }

    /**
     * 使用Hibernate Validator进行数据校验
     * @param user
     * @return
     */
    @RequestMapping("/addHaveCheck")
    public String add(User user) {
        StringBuilder stringBuilder = new StringBuilder();
        Set<ConstraintViolation<User>> validate = Validation.buildDefaultValidatorFactory().getValidator().validate(user);
        validate.forEach(validateResult -> {
//            System.out.println("错误信息:" + validateResult.getPropertyPath()+" "+validateResult.getMessage());
        stringBuilder.append(validateResult.getPropertyPath()+" "+validateResult.getMessage()+" ");
        });
        return stringBuilder.toString();
    }

    /**
     * 多线程查询 模拟缓存穿透
     */
    @RequestMapping("/threadSelect")
    public void threadSelect(){
        this.runTimes=0;
        CountDownLatch countDownLatch = new CountDownLatch(10);
        log.info("多线程访问开始");
        // 多线程模拟访问数据库
        ThreadPoolExecutor threadPoolExecutor = ThreadPoolUtils.getThreadPoolExecutor();
        for (int i = 0; i < 10; i++) {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
//                    synchronized (Runnable.class){}
                        for (int i = 0; i < 20000; i++) {
                            selectById(10L);
                        }

                    countDownLatch.countDown();
                }

            });
            // 该线程执行完成后 -1
        }

        try {
            //
            countDownLatch.await();
            log.info("当前所有线程模拟访问总次数{}",runTimes);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 拦截访问条件
        //普通拦截 第一步查询缓存
        //第二步查数据库

    }

    /**
     * 多线程模拟内存穿透
     * @param id
     */
    public synchronized void selectById(@NotNull Long id){
        this.runTimes++;
        String key="user"+id.toString();
        List<User> cacheList = redisTemp.getCacheList(key);
        if (CollectionUtils.isEmpty(cacheList)){
            User user = userMapper.selectById(id);
            if(user!=null){
                redisTemp.setCacheList(key,Arrays.asList(user));
            }
            log.info("当前数据库查出的内容:{}",user);
        }else{
            log.info("这是redis查询到的用户信息{}",cacheList);

        }
    }


}
