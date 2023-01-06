package com.purplespace.commoon;

//import org.checkerframework.checker.units.qual.K;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author loongzhang
 * @Description DOING
 * @date 2022-12-12-15:57
 */
@Component
public class RedisTemp {

    @Resource
    public RedisTemplate redisTemplate;

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final Long timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获取有效时间
     *
     * @param key Redis键
     * @return 有效时间
     */
    public long getExpire(final String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 判断 key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 判断 key是否不存在
     *
     * @param key 键
     * @return true 不存在 false存在
     */
    public Boolean notHasKey(String key) {
        return !hasKey(key);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public boolean deleteObject(final String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return
     */
    public long deleteObject(final Collection collection) {
        return redisTemplate.delete(collection);
    }

    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> long setCacheList(final String key, final List<T> dataList) {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheListRange(final String key, int start, int end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet) {
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        Iterator<T> it = dataSet.iterator();
        while (it.hasNext()) {
            setOperation.add(it.next());
        }
        return setOperation;
    }

    /**
     * 获得缓存的set
     *
     * @param key
     * @return
     */
    public <T> Set<T> getCacheSet(final String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public <T> Map<String, T> getCacheMap(final String key) {
        return redisTemplate.opsForHash().entries(key);
    }


    /**
     * 往Hash中存入数据
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> T getCacheMapValue(final String key, final String hKey) {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key   Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys) {
        return redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    /**
     * 获取多个Hash中的Key
     *
     * @param key Redis键
     * @return Hash对象集合
     */
    public Set<String> getMultiCacheMapKeys(final String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key Redis键
     * @return Hash对象集合
     */
    public Boolean deleteMultiCacheMap(final String key, final Collection<Object> hKeys) {
        if (redisTemplate.opsForHash().delete(key, hKeys) > 0) {
            return true;
        }
        return false;
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(final String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 获取指定前缀的一系列key
     * 使用scan命令代替keys, Redis是单线程处理，keys命令在KEY数量较多时，
     * 操作效率极低【时间复杂度为O(N)】，该命令一旦执行会严重阻塞线上其它命令的正常请求
     *
     * @param keyPrefix
     * @return
     */
    public Set<String> keysScan(String keyPrefix) {
        String realKey = "*" + keyPrefix + "*";
        try {
//            return (Set<String>) redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
//                Set<String> binaryKeys = new HashSet<>();
//                Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder().match(realKey).count(Integer.MAX_VALUE).build());
//                while (cursor.hasNext()) {
//                    binaryKeys.add(new String(cursor.next()));
//                }
//                return binaryKeys;
//            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除指定前缀的一系列key
     *
     * @param keyPrefix
     */
    public void removeAll(String keyPrefix) {
        try {
            Set<String> keys = keysScan(keyPrefix);
            redisTemplate.delete(keys);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用pipelined批量存储
     * @param map
     * @param seconds
     */
    public void executeMapPipelined(Map<String, String> map, long seconds) {
        RedisSerializer stringSerializer = redisTemplate.getStringSerializer();
        redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                map.forEach((key, value) -> {
                    connection.set(stringSerializer.serialize(key), stringSerializer.serialize(value)
                            , Expiration.seconds(seconds), RedisStringCommands.SetOption.UPSERT);

                });
                return null;
            }
        },stringSerializer);
    }

    public void executeStringPipelined(Object key,Object value,long seconds) {
        RedisSerializer stringSerializer = redisTemplate.getStringSerializer();
        RedisSerializer keySerializer = redisTemplate.getKeySerializer();
        RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
        redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                connection.set(keySerializer.serialize(key),valueSerializer.serialize(value),Expiration.seconds(seconds),RedisStringCommands.SetOption.UPSERT);
                /**
                 * 源码提示
                 * Callback cannot return a non-null value as it gets overwritten by the pipeline
                 * （回调无法返回非null值，因为它会被管道覆盖）
                 */
                return null;
            }
        },stringSerializer);
    }
    public void setObjectKey(Object key,Object value,long seconds){
        redisTemplate.opsForValue().set(key,value,seconds);
    }
    public Object getObjectKey(Object key){
        return redisTemplate.opsForValue().get(key);
    }
    // 以下是自定义

    /**
     * 从左边添加元素
     * @param key
     */
    public Object leftPop(String key,String value){
        return redisTemplate.opsForList().leftPush(key,value);
    }
    public Object rightPush(String key,String value){
        return redisTemplate.opsForList().rightPush(key,value);
    }
    public Object rightPop(String key){
        return redisTemplate.opsForList().rightPop(key);
    }

    public Long lSize(String key){
        return redisTemplate.opsForList().size(key);

    }
    public <T>List<T> lRange(Object key, Long start, Long end){
        return redisTemplate.opsForList().range(key, start, end);
    }
    public void bfadd(){

    }

}
