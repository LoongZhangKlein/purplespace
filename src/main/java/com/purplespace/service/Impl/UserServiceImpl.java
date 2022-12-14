package com.purplespace.service.Impl;

import com.purplespace.entity.User;
import com.purplespace.mapper.UserMapper;
import com.purplespace.service.UserService;
import com.purplespace.utils.CustomRunnable;
import com.purplespace.utils.ThreadPoolUtils;
import lombok.Synchronized;
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
public class UserServiceImpl implements UserService {
    @Resource
    UserMapper userMapper;
    @Resource
    ThreadPoolUtils threadPoolUtils;
    public static List<User> userList = new ArrayList<>();
    public static List<User> userListMain = new ArrayList<>();


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
        addThreadMethod();
        //addUserSingle();
        return 1;
    }

    public int addUserSingle() {
        log.info("单线程执行开始");
        long l = System.currentTimeMillis();
        for (int i = 0; i < 500000; i++) {
            User virtualUser = this.createVirtualUser();
            userListMain.add(virtualUser);
        }
        log.info("单线程执行结束 执行消耗时间{}", System.currentTimeMillis() - l);
        log.info("userListMain{}", userListMain.size());
        return 8849;
    }

    /**
     * 模拟多用户添加数据
     *
     * @return
     */
    private int addThreadMethod() {
        CountDownLatch countDownLatch = new CountDownLatch(5);
        ThreadPoolExecutor threadPoolExecutor = ThreadPoolUtils.getThreadPoolExecutor();
        log.info("多线程用户添加开始");
        long start = System.currentTimeMillis();
        for (int i = 0; i < 5; i++) {
            int name = i;
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    synchronized (Runnable.class) {
                        for (int j = 0; j < 100000; j++) {
                            //log.info("当前执行线程++>{}",Thread.currentThread().getName());
                            userList.add(createVirtualUser());
                            //log.info("用户信息",createVirtualUser());
                        }
                    }
                    //该线程执行完毕-1
                    countDownLatch.countDown();
                }
            });
        }
        //关闭线程处理
        try {
            countDownLatch.await();
            log.info("五个线程添加50000用户所需要时间{}", System.currentTimeMillis() - start);
            log.info("userList容量{}", userList.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //关闭线程池
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

//    @Override
//    public void run() {
//        // 创建用户
//            User virtualUser = this.createVirtualUser();
//            userList.add(virtualUser);
//
//    }

    public int getListSize() {
        return userList.size();
    }

}
