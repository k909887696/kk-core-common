package com.kk.common.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * created with IntelliJ IDEA.
 * description: 认证、登录、权限 redis配置
 * auth: kk
 * date: 2026-05-27
 */
@Data
@Component
@ConfigurationProperties(prefix = "auth-redis")
public class AuthRedisProperties {
    /**
     * 服务地址  127.0.0.1
     */
    private String host;
    /**
     * 端口 6379
     */
    private int port;
    /**
     * 密码
     */
    private String password;
    /**
     * 数据库索引
     */
    private int database;
    /**
     * jwt密钥
     */
    private String jwtSecretKey;
}
