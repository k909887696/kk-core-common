package com.kk.common.base;

import com.kk.common.base.aspect.AspectLogAdvice;
import com.kk.common.utils.MapperDateConverter;
import com.kk.common.utils.MapperUtils;
import com.kk.common.web.intercepter.ParameterIntercepter;
import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapper;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: kk
 * @Date: 2021/11/18 17:54
 * 基础注入
 */
@Configuration
public class CustomBaseConfigurer  {

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

    @Bean
    public MapperUtils mapperUtils() {
       return  new MapperUtils();
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

}
