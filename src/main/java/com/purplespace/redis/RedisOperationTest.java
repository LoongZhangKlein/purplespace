package com.purplespace.redis;

import com.purplespace.commoon.RandomWords;
import com.purplespace.commoon.RedisTempMine;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author loongzhang
 * @Description DOING
 * @date 2022-12-15-22:44
 */
@RestController
@RequestMapping("/redis")
public class RedisOperationTest {
    @Resource
    RandomWords randomWords;
    @Resource
    RedisTempMine redisTempMine;
    @RequestMapping("/test")
    public void test(){
        String key="ziDong";
        String value="紫东";
        redisTempMine.set(key,value);
        String s = redisTempMine.get(key).toString();
        System.out.println(" 当前key的值"+s);
        byte[] dump = redisTempMine.dump(key);
        System.out.println("将当前传入的key序列化:"+dump);

    }

    /**
     * 管道的理解
     * 相当于消防车的高压水枪,把水快速传输到指定位置,通过一个或多个管道
     */
    @RequestMapping("/testPipeLined")
    public void testPipeLined(){
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            redisTempMine.set(String.valueOf(i),String.valueOf(i),100L, TimeUnit.SECONDS);
        }
        System.out.println("未使用管道执行时间"+(System.currentTimeMillis()-start));
        List list = redisTempMine.executeStringPipelined();
        System.out.println(list.get(0));
    }
    @RequestMapping("/testRedis")
    public void testRedisBaseOperation(){
        List<String> wordsList = RandomWords.getChineseList(100);
        for (int i = 0; i < 5; i++) {
            String randomWords1 = RandomWords.getRandomWords(4, 1);
            System.out.println(randomWords1);
        }


//        String key="test";
//        String value="value";
//        redisTempMine.set(key,value);

    }
}
