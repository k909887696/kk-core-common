package com.kk.common.redis;


import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;
/**
 * created with IntelliJ IDEA.
 * description: 认证、登录、权限 Redis 工具类
 * auth: kk
 * date: 2026-05-27
 */


@Component
@ConditionalOnProperty(prefix = "auth-redis", name = "host")
public class AuthRedisUtil {

    // 精准注入我们刚刚独立初始化的 authRedisTemplate
    @Autowired
    @Resource(name="authRedisTemplate")
    private RedisTemplate<String, Object> authRedisTemplate;

    private static final String TOKEN_PREFIX = "auth:token:";
    private static final String LOGIN_ERROR_PREFIX = "auth:error:";

    /**
     * 保存用户登录 Token
     */
    public void saveToken(String userId, String token, long expireSec) {
        String key = TOKEN_PREFIX + userId;
        authRedisTemplate.opsForValue().set(key, token, expireSec, java.util.concurrent.TimeUnit.SECONDS);
    }

    /**
     * 获取用户登录 Token
     */
    public String getToken(String userId) {
        String key = TOKEN_PREFIX + userId;
        Object value = authRedisTemplate.opsForValue().get(key);
        return value == null ? null : value.toString();
    }

    /**
     * 删除/注销用户 Token
     */
    public void deleteToken(String userId) {
        String key = TOKEN_PREFIX + userId;
        authRedisTemplate.delete(key);
    }

    /**
     * 记录登录错误次数（用于防暴力破解，例如锁定 10 分钟）
     */
    public void incrementLoginError(String username, long lockMinutes) {
        String key = LOGIN_ERROR_PREFIX + username;
        Long count = authRedisTemplate.opsForValue().increment(key);
        // 如果是第一次错误，设置过期时间
        if (count != null && count == 1L) {
            authRedisTemplate.expire(key, lockMinutes, java.util.concurrent.TimeUnit.MINUTES);
        }
    }

    /**
     * 获取当前登录错误次数
     */
    public Integer getLoginErrorCount(String username) {
        String key = LOGIN_ERROR_PREFIX + username;
        Object count = authRedisTemplate.opsForValue().get(key);
        return count == null ? 0 : Integer.parseInt(count.toString());
    }
}
