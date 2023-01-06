package com.purplespace.commoon;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author loongzhang
 * @Description DOING
 * @date 2022-12-20-17:45
 */
@Component
public class RedisTempMine<K,V> {
    @Resource
    private RedisTemplate redisTemplate;
    public void set(Object k,Object v){
        redisTemplate.opsForValue().set(k,v);
    }
    public void set(String k,Object v){
        redisTemplate.opsForValue().set(k,v);
    }
    public void set(String k,String v){
        redisTemplate.opsForValue().set(k,v);
    }
    public void set(Object k,Object v,Long time,TimeUnit timeUnit){
        redisTemplate.opsForValue().set(k,v,time,timeUnit);
    }
    public Boolean hasKey(String key){
        return redisTemplate.hasKey(key);
    }
    public  Object get(String k){
        return redisTemplate.opsForValue().get(k);
    }
    public Boolean del(String k){
        return redisTemplate.delete(k);
    }

    public <K>Long delBatch(Collection<K> keys){
        return redisTemplate.delete(keys);
    }
    /**
     * 将当前传入的key序列化为byte类型
     */
    public byte[] dump(String key){
        return redisTemplate.dump(key);
    }

    /**
     *设置key的过期时间
     * @param k
     * @param time
     * @param timeUnit
     * @return
     */
    public boolean expire(String k, Long time, TimeUnit timeUnit){
        return redisTemplate.expire(k, time, timeUnit);
    }
    /**
     * 重命名一个key
     * @param oldKey
     * @param newKey
     */
    public void reName(String oldKey,String newKey){
        redisTemplate.rename(oldKey,newKey);
    }

    /**
     * 旧值存在,用新值覆盖
     */
    public void reNameIfAbsent(String oldKey,String newKey){
        redisTemplate.renameIfAbsent(oldKey,newKey);
    }

    /**
     * 返回所存储值的类型
     * @param key
     * @return
     */
    public DataType type(String key){
        return redisTemplate.type(key);
    }
    /**
     *返回任意key
     * 使用场景在用户里边抽奖
     * 返回一个key将幸运用户删除
     */
    public Object randomKey(){
        return redisTemplate.randomKey();
    }
    public Long getExpire(String key){
        return redisTemplate.getExpire(key);
    }

    /**
     * 将key持久化保存
     * @param key
     * @return
     */
    public Boolean persist(String key){
        return redisTemplate.persist(key);
    }
    public boolean move(String key,Integer dbIndex){
        return redisTemplate.move(key, dbIndex);
    }

    /**
     * 将旧的Key设置为value,并且返回旧的Key
     * @param key
     * @param value
     * @return
     */
    public Object getAndSet(String key,String value){
        return redisTemplate.opsForValue().getAndSet(key, value);
    }

    /**
     * 批量获取值
     * @param keys
     */
    public void multiGet(String... keys){
        List<String> list = Arrays.asList(keys);
        redisTemplate.opsForValue().multiGet(list);
    }

    /**
     * 在原有基础上新增字符创到尾部
     * @param key
     * @param value
     * @param <K>
     */
    public<K> void append(K key,String value){
        redisTemplate.opsForValue().append(key,value);
    }

    /**
     * 给key对应的value进行自增
     * @param key
     */
    public void increment(Object key){
        redisTemplate.opsForValue().increment(key);
    }

    /**
     * 给key对应的对应的value进行指定步长的自增
     * @param key
     * @param delta
     */
    public void increment(Object key,Long delta){
        redisTemplate.opsForValue().increment(key,delta);
    }
//    public void multiSetIfAbsent();

    /**
     * 使用管道发送String类型数据
     * @param
     * @param
     * @param
     */
    public List<Long> executeStringPipelined(){
        RedisSerializer stringSerializer = redisTemplate.getStringSerializer();
        List<Long> list=redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                // 打开流操作
                connection.openPipeline();
                long start = System.currentTimeMillis();
                for (int i = 0; i < 10000; i++) {
                    connection.set(stringSerializer.serialize(String.valueOf(i)),stringSerializer.serialize(String.valueOf(i))
                            , Expiration.seconds(1000), RedisStringCommands.SetOption.UPSERT);
                }
                System.out.println("使用管道执行时间"+(System.currentTimeMillis()-start));
              return null;
            }
        },stringSerializer);
        return list;
    }

    /**
     * 使用管道发送Map类型数据
     * @param stringMap
     * @param seconds
     */
    public void executeMapPipelined(Map<String,String> stringMap, long seconds){
        RedisSerializer stringSerializer = redisTemplate.getStringSerializer();

        redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                stringMap.forEach((key,value)->{
                    connection.set(stringSerializer.serialize(key),stringSerializer.serialize(value),Expiration.seconds(seconds), RedisStringCommands.SetOption.UPSERT);
                });
                return null;
            }
        },stringSerializer);
    }
    public void redis(){
//        redisTemplate.executePipelined(new SessionCallback<Object>() {
//            @Override
//            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
//                RedisTemp ops= operations();
//                return null;
//            }
//        })
    }
    public void bgSave(){

    }


}
