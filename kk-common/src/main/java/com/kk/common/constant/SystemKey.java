package com.kk.common.constant;

/**
 * created with IntelliJ IDEA.
 * description: 系统键值对、默认值定义
 * auth: kk
 * date: 2026-05-11
 */
public class SystemKey {
    /**
     * 请求开始时间key
     */
    public final static String REQUEST_START_TIME="request_start_time";
    /**
     * 全链路日志跟踪key
     */
    public final static String TRACE_ID_KEY="traceId";

    /**
     * 慢接口阈值（毫秒）
     */
    public final static long SLOW_THRESHOLD = 1000;
    /**
     * 编码格式
     */
    public final static String ENCODING = "UTF-8";
    /**
     * 默认日期格式
     */
    public final static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 日志存储配置es客户端key
     */
    public final static String LOGGING_STORE = "logging_store";

    /**
     * 日志存储key
     */
    public final static String LOGGING_STORE_KEY = "logKey";
    /**
     * 默认es客户端
     */
    public final static String ES_DEFAULT_CLIENT = "default";
    /**
     * 登录用户缓存key
     */
    public final static String LoginUserCacheKey = "userInfo";
    /**
     * 登录用户权限缓存key
     */
    public final static String LoginUserJurisdictionCacheKey = "userJur";

    /**
     * 不需要登录的接口前缀
     */
    public final static String ApiPrefixNoLogin = "nl/nlapi";

    /**
     * 需要登录的接口前缀
     */
    public final static String ApiPrefixLogin = "v/vapi";

    /**
     * 需要签名的接口前缀
     */
    public final static String ApiPrefixSign = "ext/extapi";

    /**
     * 内部接口走feign 调用
     */
    public final static String ApiPrefixInn = "inn/innapi";


}
