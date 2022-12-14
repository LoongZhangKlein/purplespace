package com.purplespace.utils;

import com.purplespace.entity.User;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * @author loongzhang
 * @Description DOING
 * @date 2022-12-12-17:07
 */
@Component
public class ThreadPoolUtils<T> {
    private static final Runnable T =null;
    private T t;
    //核心线程数
    private static final int corePoolSize = 5;
    // 线程池最大线程数
    private static final int maxPoolSize = 10;
    //
    private static final int queueCapacity = 100;
    // 多余线程存活时间
    private static final Long keepAliveTime = 1L;
    private static ThreadPoolExecutor  threadPoolExecutor;

    private ThreadPoolUtils(){

    }
    public static ThreadPoolExecutor getThreadPoolExecutor(){
        if (threadPoolExecutor==null){
            synchronized (ThreadPoolUtils.class){
                if (threadPoolExecutor==null){
                    threadPoolExecutor= new ThreadPoolExecutor(
                            corePoolSize,
                            maxPoolSize,
                            keepAliveTime,
                            TimeUnit.SECONDS,
                            new ArrayBlockingQueue<>(queueCapacity),
                            new ThreadPoolExecutor.CallerRunsPolicy());
                }
            }

        }
        return threadPoolExecutor;
    }
}
