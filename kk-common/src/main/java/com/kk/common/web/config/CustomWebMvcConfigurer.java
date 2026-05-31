package com.kk.common.web.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.kk.common.constant.ResponseCode;
import com.kk.common.web.filter.MyServletRequestReplacedFilter;
import com.kk.common.web.filter.BaseFilter;
import com.kk.common.web.intercepter.ParameterInterceptor;
import com.kk.common.web.intercepter.SignatureInterceptor;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.ReflectionUtils;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: kk
 * @Date: 2021/11/18 17:54
 * 拦截器 、监听器注册
 */
@Configuration
public class CustomWebMvcConfigurer implements WebMvcConfigurer {

    @Value("${custom-web-mvc-config.interceptor-parameter-pattern:/**}")
    public  String interceptorParameterPattern;

    @Value("${custom-web-mvc-config.interceptor-login-pattern:/**}")
    public  String interceptorLoginPattern;

    @Value("${custom-web-mvc-config.interceptor-signature-pattern:/**/ext/extapi/**}")
    public  String interceptorSignaturePattern;

    @Value("${custom-web-mvc-config.interceptor-no-login-pattern:/**/nl/nlapi/**}")
    public  String interceptorNoLoginPattern;

    /**
     * 添加拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册多个Interceptor  注意路径的写法
        registry.addInterceptor( parameterInterceptor()).addPathPatterns(interceptorParameterPattern).order(0);
        //添加签名拦截器
        registry.addInterceptor( signatureInterceptor()).addPathPatterns(interceptorSignaturePattern).order(1);
        //sa-token 拦截器
        // ⚠️ 必须注册 SaInterceptor，否则 @SaCheckPermission 等注解全部失效！
        registry.addInterceptor(new SaInterceptor()// 【1. 前置日志】每次请求进入鉴权前触发
                        // 【2. 核心鉴权逻辑】在这里调用 StpUtil.checkLogin()
                        .setAuth(req -> {
                            // 使用 SaRouter 灵活划分路由规则
                            SaRouter.match("/**")
                                    // 排除掉不需要登录和鉴权的公开接口
                                    //.notMatch("/**/login", "/auth/register", "/error")
                                    // 执行登录校验（如果未登录会抛出 NotLoginException，被下面的 setError 捕获）
                                    .check(r -> StpUtil.checkLogin());
                            // 你可以在这里继续扩展其他模块的权限校验
                            // SaRouter.match("/admin/**", r -> StpUtil.checkPermission("admin"));
                        })
                )
                .addPathPatterns(interceptorLoginPattern)
                .excludePathPatterns(interceptorSignaturePattern)
                .excludePathPatterns(interceptorNoLoginPattern)
                .excludePathPatterns("/swagger-ui/**");

        //通用拦截器排除设置，所有拦截器都会自动加springdoc-opapi相关的资源排除信息，不用在应用程序自身拦截器定义的地方去添加，算是良心解耦实现。
        Field registrationsField = FieldUtils.getField(InterceptorRegistry.class, "registrations", true);
        List<InterceptorRegistration> registrations = (List<InterceptorRegistration>) ReflectionUtils.getField(registrationsField, registry);
        if (registrations != null) {
            for (InterceptorRegistration interceptorRegistration : registrations) {
                interceptorRegistration.excludePathPatterns("/springdoc**/**");
            }
        }
        WebMvcConfigurer.super.addInterceptors(registry);
    }


    @Bean(name = "parameterInterceptor")
    public ParameterInterceptor parameterInterceptor() {
        ParameterInterceptor parameterInterceptor = new ParameterInterceptor();
        return parameterInterceptor;
    }

    @Bean(name = "signatureInterceptor")
    public SignatureInterceptor signatureInterceptor() {
        SignatureInterceptor signatureInterceptor = new SignatureInterceptor();
        return signatureInterceptor;
    }
    /**
     * 跨域配置
     * Spring Boot 3 使用 CorsFilter 方式配置跨域
     *
     * @author kk
     * @since 2026-04-22
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 允许所有域名进行跨域调用（生产环境建议指定具体域名）
        config.addAllowedOriginPattern("*");

        // 允许跨域发送cookie
        config.setAllowCredentials(false);

        // 允许所有请求头
        config.addAllowedHeader("*");

        // 允许所有请求方法
        config.addAllowedMethod("*");

        // 暴露响应头
        config.addExposedHeader("*");

        // 预检请求的有效期，单位为秒
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径生效
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    /**
     * 替换servlet 兼容多次读取流问题 过滤器 - 最高优先级
     */
    @Bean
    public FilterRegistrationBean<MyServletRequestReplacedFilter> myServletRequestReplacedFilter() {
        FilterRegistrationBean<MyServletRequestReplacedFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new MyServletRequestReplacedFilter());
        registration.setName("MyServletRequestReplacedFilter");
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
    /**
     * 基础过滤器 - 第二优先级
     */
    @Bean
    public FilterRegistrationBean<BaseFilter> baseFilter() {
        FilterRegistrationBean<BaseFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new BaseFilter());
        registration.setName("BaseFilter");
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return registration;
    }


    private final SwaggerProperties swaggerProperties;

    public CustomWebMvcConfigurer(SwaggerProperties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
    }

    /**
     * open api doc config
     * @return
     */
    @Bean
    public OpenAPI springDocOpenAPI() {
        //基础响应码
        String ResponseCodeMsg = Arrays.stream(ResponseCode.values())
                .map(code -> code.getCode() + ":" + code.getDesc())
                .collect(Collectors.joining("<br>"));

        // 接口调试路径
        List<Server> servers = new java.util.ArrayList<>();
        Server tryServer = new Server();
        tryServer.setUrl(swaggerProperties.getTryHost());
        servers.add(tryServer);
        StringBuilder swaggerDescription = new StringBuilder();
        swaggerDescription.append("服务名称：").append(swaggerProperties.getApplicationDescription()).append("<br>");
        swaggerDescription.append("响应码：").append(ResponseCodeMsg).append("<br>");
        return new OpenAPI()
                //.servers(servers)
                .info(new Info()
                        .title(swaggerProperties.getApplicationName() + " Api Doc")
                        .summary(swaggerProperties.getApplicationDescription())
                        .description(swaggerDescription.toString())
                        .version("Application Version: " + swaggerProperties.getApplicationVersion() + "\n Spring Boot Version: " + SpringBootVersion.getVersion())
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                )
                .externalDocs(new ExternalDocumentation()
                        .description("SpringDoc Full Documentation")
                        .url("https://springdoc.org/")
                );

    }
}
