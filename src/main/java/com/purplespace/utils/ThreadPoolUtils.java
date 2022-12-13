package com.purplespace.utils;

import com.purplespace.entity.User;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * @author loongzhang
 * @Description DOING
 * @date 2022-12-12-17:07
 */
class ThreadTask implements Runnable {
    public ThreadTask() {

    }

    @Override
    public void run() {

    }
}

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
    private ThreadPoolExecutor threadPoolExecutor = null;

    public ThreadPoolExecutor threadPoolExecutor() {
        if (threadPoolExecutor != null) {
            return this.threadPoolExecutor;
        }
        threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.CallerRunsPolicy());
        return threadPoolExecutor;
    }
    public void startCustomThread(int threadNumb,T t){
        this.t=t;
        for (int i = 0; i < threadNumb; i++) {
                // 创建runnable接口
                threadPoolExecutor.submit(T);
                // 执行线

        }
    }
    public int activeThreadNumb(){
        if (threadPoolExecutor==null){
            return -1;
        }
        return threadPoolExecutor.getActiveCount();
    }

    public ThreadPoolExecutor threadPoolExecutor(int corePoolSize, int maxPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        if (threadPoolExecutor != null) {
            return this.threadPoolExecutor;
        }
        threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                unit,
                new ArrayBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.CallerRunsPolicy());
        return threadPoolExecutor;
    }
}
