package com.purplespace.redis;

import com.purplespace.utils.ThreadPoolUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author loongzhang
 * @Description DOING
 * @date 2023-01-03-10:01
 */
@RequestMapping("/cacheBrakeDown")
public class CacheBrakeDown {
    /**
     * 缓存击穿测试
     */
    public void cacheBrakeDown(){
        // 多线程模拟访问数据库
        ThreadPoolExecutor threadPoolExecutor = ThreadPoolUtils.getThreadPoolExecutor();
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 1000; i++) {

                }
            }
        });
        // 拦截访问条件
            //普通拦截 第一步查询缓存
                //第二步查数据库

    }

}
