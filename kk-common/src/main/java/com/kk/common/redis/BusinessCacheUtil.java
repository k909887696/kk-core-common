package com.kk.common.redis;

/**
 * created with IntelliJ IDEA.
 * description: 业务缓存操作工具类
 * auth: kk
 * date: 2026-05-27
 */
import org.ehcache.xml.model.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


@Component
@ConditionalOnProperty(prefix = "business-redis", name = "nodes")
public class BusinessCacheUtil {

    @Autowired
    private BusinessRedisFactory businessRedisFactory;

    /**
     * 向指定的业务 Redis 存入缓存
     * @param bizName 业务名称 (order/product/activity)
     * @param key 缓存键
     * @param value 缓存值
     * @param expireSeconds 过期时间(秒)
     */
    public void setCache(String bizName, String key, Object value, long expireSeconds) {
        RedisTemplate<String, Object> template = businessRedisFactory.getTemplate(bizName);
        template.opsForValue().set(key, value, expireSeconds, java.util.concurrent.TimeUnit.SECONDS);
    }

    /**
     * 从指定的业务 Redis 获取缓存
     */
    public Object getCache(String bizName, String key) {
        RedisTemplate<String, Object> template = businessRedisFactory.getTemplate(bizName);
        return template.opsForValue().get(key);
    }

    /**
     * 删除指定业务 Redis 的缓存
     */
    public void deleteCache(String bizName, String key) {
        RedisTemplate<String, Object> template = businessRedisFactory.getTemplate(bizName);
        template.delete(key);
    }
}
