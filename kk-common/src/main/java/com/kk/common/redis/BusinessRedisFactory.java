package com.kk.common.redis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * created with IntelliJ IDEA.
 * description: 业务redis工厂类，多客户端
 * auth: kk
 * date: 2026-05-27
 */
@Component
public class BusinessRedisFactory {

    @Autowired
    private BusinessRedisProperties redisProperties;

    // 使用 ConcurrentHashMap 缓存已经创建好的 RedisTemplate，避免重复创建连接
    private final Map<String, RedisTemplate<String, Object>> templateCache = new ConcurrentHashMap<>();

    /**
     * 根据业务名称获取对应的 RedisTemplate
     * @param bizName 业务名称，如 "order", "product"
     */
    public RedisTemplate<String, Object> getTemplate(String bizName) {
        return templateCache.computeIfAbsent(bizName, this::createRedisTemplate);
    }

    private RedisTemplate<String, Object> createRedisTemplate(String bizName) {
        BusinessRedisProperties.NodeConfig config = redisProperties.getNodes().get(bizName);
        if (config == null) {
            throw new IllegalArgumentException("未找到名为 [" + bizName + "] 的业务Redis配置");
        }

        // 1. 创建连接配置
        RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration();
        standaloneConfig.setHostName(config.getHost());
        standaloneConfig.setPort(config.getPort());
        standaloneConfig.setPassword(config.getPassword());
        standaloneConfig.setDatabase(config.getDatabase());

        // 2. 创建 Lettuce 连接工厂
        LettuceConnectionFactory factory = new LettuceConnectionFactory(standaloneConfig);
        factory.afterPropertiesSet(); // 必须调用初始化

        // 3. 创建并配置 RedisTemplate
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        // 推荐使用 String 序列化 Key，JSON 序列化 Value，方便在 Redis 客户端查看
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();

        return template;
    }
}
