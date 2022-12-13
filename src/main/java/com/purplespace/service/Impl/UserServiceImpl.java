package com.purplespace.service.Impl;

import com.purplespace.entity.User;
import com.purplespace.mapper.UserMapper;
import com.purplespace.service.UserService;
import com.purplespace.utils.CustomRunnable;
import com.purplespace.utils.ThreadPoolUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author loongzhang
 * @Description DOING
 * @date 2022-12-12-16:29
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService{
    @Resource
    UserMapper userMapper;
    @Resource
    ThreadPoolUtils threadPoolUtils;
    private static List<User> userList = new ArrayList<>();
    private static List<User> userListMain = new ArrayList<>();


    public int addUser(User user) {
        if (user == null) {
            throw new RuntimeException("添加数据为空");
        }
        int insert = userMapper.insert(user);

        return insert;
    }
    @Override
    public int add() {
        userList.clear();
        userListMain.clear();
        log.info("当前userListMain容量"+userListMain.size());
        addUserSingle();
        log.info("单线程执行完成后userListMain容量"+userListMain.size());
        addThreadMethod();

        return 1;
    }
    public int addUserSingle() {
        log.info("单线程执行开始");
        long l = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            User virtualUser = this.createVirtualUser();
            userListMain.add(virtualUser);
        }
        log.info("单线程执行结束 执行消耗时间{}",System.currentTimeMillis()-l);
        return 8849;
    }

    /**
     * 模拟多用户添加数据
     *
     * @return
     */
    private int addThreadMethod() {
        ThreadPoolExecutor threadPoolExecutor = threadPoolUtils.threadPoolExecutor();
        long l = System.currentTimeMillis();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("多线程执行开始 当前时间{}",Thread.currentThread().getName(),df.format(new Date()));
        for (int i = 0; i < 10000; i++) {
            threadPoolExecutor.execute(()->{
                User virtualUser = this.createVirtualUser();
                userList.add(virtualUser);
            });
        }
        while (threadPoolExecutor.getActiveCount()<=0){
            threadPoolExecutor.shutdown();
            log.info("当前userList容量{} 执行消耗时间{}",userList.size(),System.currentTimeMillis()-l);
            return 0;
        }
        //
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

//    @Override
//    public void run() {
//        // 创建用户
//            User virtualUser = this.createVirtualUser();
//            userList.add(virtualUser);
//
//    }

    public int getListSize(){
        return userList.size();
    }
}
