package com.purplespace.utils;

import com.purplespace.entity.User;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author loongzhang
 * @Description DOING
 * @date 2022-12-13-9:37
 */
@Slf4j
public class CustomRunnable<T> implements Runnable{

    private T t;
    private List<T> list;
    public CustomRunnable(){
    }
    public CustomRunnable(T t){
        this.t=t;
    }

    public void customTask(T t){
        this.t=t;
        User user= (User) t;
        log.info("用户信息:"+user);
    }
    public List createListCache(T t){
        this.t=t;
        list=new ArrayList<>();
        return list;
    }
    @Override
    public void run() {
        log.info("这里是当前执行的线程",Thread.currentThread().getName()+"startTime",new Date());
        this.customTask(t);
    }
}
