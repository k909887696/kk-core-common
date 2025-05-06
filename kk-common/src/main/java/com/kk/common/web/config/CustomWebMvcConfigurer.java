package com.kk.common.web.config;

import com.kk.common.web.intercepter.LoginIntercepter;
import com.kk.common.web.intercepter.ParameterIntercepter;
import com.kk.common.web.listener.ApplicationStartedEventListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.PublicKey;
import java.util.List;

/**
 * @Author: kk
 * @Date: 2021/11/18 17:54
 * 拦截器 、监听器注册
 */
@Configuration
public class CustomWebMvcConfigurer implements WebMvcConfigurer {

    @Value("${custom-web-mvc-config.intercepter-parameter-pattern:/api/**}")
    public  String intercepterParameterPattern;

    @Value("${custom-web-mvc-config.intercepter-login-pattern:/**/api/**}")
    public  String intercepterLoginPattern;
    @Value("${custom-web-mvc-config.intercepter-no-login-pattern:/**/napi/**}")
    public  String intercepterNoLoginPattern;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册多个Interceptor  注意路径的写法
        registry.addInterceptor( parameterIntercepter()).addPathPatterns(intercepterParameterPattern);
       // registry.addInterceptor(new TwoIntercepter()).addPathPatterns("/api2/*/**");
        //注册某个拦截器的时候，同时排除某些不拦截的路径
        registry.addInterceptor( loginIntercepter()).addPathPatterns(intercepterLoginPattern).excludePathPatterns(intercepterNoLoginPattern);
        WebMvcConfigurer.super.addInterceptors(registry);
    }


    @Bean(name = "parameterIntercepter")
    public ParameterIntercepter parameterIntercepter() {
        ParameterIntercepter parameterIntercepter = new ParameterIntercepter();
        return parameterIntercepter;
    }
    @Bean(name = "loginIntercepter")
    public LoginIntercepter loginIntercepter() {
        LoginIntercepter loginIntercepter = new LoginIntercepter();
        return loginIntercepter;
    }
    //解决上传文件控制层接收为null的方法
    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        return multipartResolver;
    }
}
