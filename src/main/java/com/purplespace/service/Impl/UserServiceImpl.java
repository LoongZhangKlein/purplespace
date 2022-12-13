package com.purplespace.service.Impl;

import com.purplespace.entity.User;
import com.purplespace.mapper.UserMapper;
import com.purplespace.service.UserService;
import com.purplespace.utils.CustomRunnable;
import com.purplespace.utils.ThreadPoolUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author loongzhang
 * @Description DOING
 * @date 2022-12-12-16:29
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService, Runnable {
    @Resource
    UserMapper userMapper;
    @Resource
    ThreadPoolUtils threadPoolUtils;
    private List<User> userList = new ArrayList<>();


    public int addUser(User user) {
        if (user == null) {
            throw new RuntimeException("添加数据为空");
        }
        int insert = userMapper.insert(user);
        return insert;
    }
    @Override
    public int add() {

        return 1;
    }
    public int createUserCustomer() throws ExecutionException, InterruptedException {
        addThreadMethod();
        return 1;
    }

    /**
     * 模拟多用户添加数据
     *
     * @return
     */
    private int addThreadMethod() {
        ThreadPoolExecutor threadPoolExecutor = threadPoolUtils.threadPoolExecutor();
        for (int i = 0; i < 10; i++) {
            // 创建runnable接口
            CustomRunnable<User> customRunnable = new CustomRunnable<>(this.createVirtualUser());
            threadPoolExecutor.submit(customRunnable);

            // 执行线
        }
        // 关闭线程
        //threadPoolExecutor.shutdown();
        return 1;
    }

    /**
     * 模拟用户信息
     */
    private User createVirtualUser() {
        Random random = new Random();
        User user = new User();
        user.setId(random.nextLong());
        user.setName(UUID.randomUUID().toString().split("-").toString());
        user.setAge(random.nextInt(10));
        user.setDeleteFlag(0);
        Date date = new Date();
        user.setCreateTime(date);
        user.setUpdateTime(date);
        return user;
    }

    @Override
    public void run() {
        // 创建用户
        for (int i = 0; i < 100; i++) {
            User virtualUser = this.createVirtualUser();
            userList.add(virtualUser);
        }
    }
}
