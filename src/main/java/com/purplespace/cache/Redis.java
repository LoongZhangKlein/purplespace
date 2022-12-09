package com.purplespace.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author loongzhang
 * @Description DOING
 * @date 2022-12-09-15:04
 */

@Configuration
public class Redis {
    @Autowired
    private final RedisTemplate redisTemplate;

    public Redis(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void delete(String key){
        redisTemplate.delete(key);
    }

    /**
     * 删除多个key
     * @param keys
     */
    public void deleteKey (String ...keys){
        redisTemplate.delete(keys);
    }

    //    指定key的失效时间
    public void expire(String key,long time){
        redisTemplate.expire(key,time, TimeUnit.MINUTES);
    }


    //    根据key获取过期时间
    public long getExpire(String key){
        Long expire = redisTemplate.getExpire(key);
        return expire;
    }

    //    判断key是否存在
    public boolean hasKey(String key){
        return redisTemplate.hasKey(key);
    }

    public void setKey(String k,String v) {
        redisTemplate.opsForValue().set(k,v);
    }
    public Object getKey(String key){
        return redisTemplate.opsForValue().get(key);
    }
}
