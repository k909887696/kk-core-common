package com.kk.common.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.Map;
/**
 * created with IntelliJ IDEA.
 * description:
 * auth: kk
 * date: 2026-05-27
 */
@Data
@Component
@ConfigurationProperties(prefix = "business-redis")
public class BusinessRedisProperties {
    // 对应 yml 中的 business-redis.nodes
    private Map<String, NodeConfig> nodes;

    @Data
    public static class NodeConfig {
        /**
         * redis节点 127.0.0.1
         */
        private String host;
        /**
         * redis节点 6379
         */
        private int port;
        /**
         * redis密码
         */
        private String password;
        /**
         * redis数据库索引
         */
        private int database;
    }
}