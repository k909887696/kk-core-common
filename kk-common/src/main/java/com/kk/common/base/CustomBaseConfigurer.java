package com.kk.common.base;


import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import com.kk.common.base.aspect.AspectLogAdvice;
import com.kk.common.base.config.BaseConfing;
import com.kk.common.constant.SystemKey;
import com.kk.common.es.EsClientFactory;
import com.kk.common.es.BusinessEsProperties;

import com.kk.common.redis.AuthRedisProperties;
import com.kk.common.utils.MapperDateConverter;
import com.kk.common.utils.MapperUtils;
import com.kk.common.web.intercepter.ParameterInterceptor;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapper;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: kk
 * @Date: 2021/11/18 17:54
 * 基础注入
 */
@Configuration
public class CustomBaseConfigurer  {

    @Resource
    private BaseConfing baseConfing;
   // This line is unnecessary if you are not using BusinessEsProperties anywhere in the class

    @Bean(name = "org.dozer.Mapper")
    public DozerBeanMapper dozer() {
        //这里是配置文件的路径
       // List<String> mappingFiles = Arrays.asList("dozer/dozer-mapping.xml");
        DozerBeanMapper dozerBean = new DozerBeanMapper();
        List<CustomConverter> converters = new  ArrayList<CustomConverter>();
        converters.add(new MapperDateConverter());
        dozerBean.setCustomConverters(converters);
        //dozerBean.setMappingFiles(mappingFiles);
        return dozerBean;
    }
    @Bean(name = "esClientFactory")
    public EsClientFactory esClientFactory(BusinessEsProperties esConfig) {
        EsClientFactory.addClient(SystemKey.LOGGING_STORE,loggingStoreClient());
        EsClientFactory.init(esConfig);
        return new EsClientFactory();
    }


    /**
     * 日志 ES 客户端 Bean（专用）
     */
    @Bean(name = "loggingStoreClient")
    public RestHighLevelClient loggingStoreClient() {
        if (baseConfing.getLoggingStoreHost() == null || baseConfing.getLoggingStoreHost().isEmpty()) {
            throw new IllegalStateException("logging.store.host is required for log ES client");
        }

        HttpHost httpHost =  HttpHost.create(baseConfing.getLoggingStoreHost());;
        RestClientBuilder builder = RestClient.builder(httpHost);

        // 设置超时
        builder.setRequestConfigCallback(requestConfigBuilder ->
                requestConfigBuilder
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setConnectionRequestTimeout(5000)
        );

        // 认证
        if (baseConfing.getLoggingStoreUserName() != null && !baseConfing.getLoggingStoreUserName().isEmpty() && baseConfing.getLoggingStorePassword() != null) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(baseConfing.getLoggingStoreUserName(), baseConfing.getLoggingStorePassword()));
            builder.setHttpClientConfigCallback(httpClientBuilder ->
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        }

        org.elasticsearch.client.RestHighLevelClient transport = new org.elasticsearch.client.RestHighLevelClient(builder);
        return transport;
    }


    @Bean
    public MapperUtils mapperUtils() {
       return  new MapperUtils();
    }
    @PostConstruct
    public void init() {

        initLoggingProperty();
    }

    /**
     * 初始化日志属性
     */
    private void initLoggingProperty() {
        if (baseConfing.getLoggingFilePath() == null || baseConfing.getLoggingFilePath().isEmpty()) {
            return;
        }
        // 1. 更新系统属性
        System.setProperty("LOG_PATH", baseConfing.getLoggingFilePath());
        System.setProperty("loggingStoreHost", baseConfing.getLoggingStoreHost());
        System.setProperty("loggingStoreIndex", baseConfing.getLoggingStoreIndex());
        System.setProperty("loggingStoreUserName", baseConfing.getLoggingStoreUserName());
        System.setProperty("loggingStorePassword", baseConfing.getLoggingStorePassword());
        System.setProperty("loggingStoreName", baseConfing.getLoggingStoreName());
        System.setProperty("loggingStoreEnv", baseConfing.getLoggingStoreEnv());
        // 2. 重新配置 Log4j2 上下文
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        context.reconfigure();

    }

    /**
     * 系统环绕日志
     * @return
     */
    @Bean
    public DefaultPointcutAdvisor defaultPointcutAspectLogAdvice(AspectLogAdvice aspectLogAdvice) {
        //AspectLogAdvice interceptor = new AspectLogAdvice();
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(aspectLogAdvice.aspectLogPointcutExpression);
        // 配置增强类advisor
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(pointcut);
        advisor.setAdvice(aspectLogAdvice);

        return advisor;
    }


    /**
     * 认证、登录、权限 redis Template Bean
     * @param authRedisProperties
     * @return
     */
    @Bean(name = "authRedisTemplate")
    public RedisTemplate<String, Object> authRedisTemplate(AuthRedisProperties authRedisProperties) {
        // 1. 配置单机 Redis 连接信息
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(authRedisProperties.getHost());
        config.setPort(authRedisProperties.getPort());
        config.setPassword(authRedisProperties.getPassword());
        config.setDatabase(authRedisProperties.getDatabase());

        // 2. 创建 Lettuce 连接工厂（Spring Boot 3 默认高性能客户端）
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        factory.afterPropertiesSet();

        // 3. 创建并配置 RedisTemplate
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        // Key 使用 String 序列化，Value 使用 JSON 序列化，方便在 Redis 客户端查看和维护
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();

        return template;
    }
    // 注入 Simple 简单模式的 JWT 逻辑处理器
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }
}
