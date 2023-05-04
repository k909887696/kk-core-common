package com.kk.common.web.config;

import com.kk.common.constant.ResponseCode;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseBuilder;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.AbstractDocumentationPluginsBootstrapper;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: kk
 * @Date: 2021/11/15 16:51
 */
@Component
@ConfigurationProperties("swagger")
@Configuration
@EnableOpenApi
@RefreshScope
public class SwaggerConfiguration implements WebMvcConfigurer {
    /**
     * 是否开启swagger，生产环境一般关闭，所以这里定义一个变量
     */
    private Boolean enable;

    /**
     * 项目应用名
     */
    private String applicationName;

    /**
     * 项目版本信息
     */
    private String applicationVersion;

    /**
     * 项目描述信息
     */
    private String applicationDescription;

    /** 通用拦截器排除swagger设置，所有拦截器都会自动加swagger相关的资源排除信息 */
    @SuppressWarnings("unchecked")
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        try {
            Field registrationsField =
                    FieldUtils.getField(InterceptorRegistry.class, "registrations", true);
            List<InterceptorRegistration> registrations =
                    (List<InterceptorRegistration>) ReflectionUtils.getField(registrationsField, registry);
            if (registrations != null) {
                for (InterceptorRegistration interceptorRegistration : registrations) {
                    interceptorRegistration
                            .excludePathPatterns("/swagger**/**")
                            .excludePathPatterns("/webjars/**")
                            .excludePathPatterns("/v3/**")
                            .excludePathPatterns("/doc.html");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.OAS_30)
                .pathMapping("/")
                // 定义是否开启swagger，false为关闭，可以通过变量控制，线上关闭
                .enable(enable)
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                //.apis(RequestHandlerSelectors.basePackage("com.kk.api.*.controller"))
                .paths(PathSelectors.any()).build()
                // 如何保护我们的Api，有三种验证（ApiKey, BasicAuth, OAuth）
                .securitySchemes(security())
                //全局控制token
                .securityContexts(Collections.singletonList(SecurityContext.builder().securityReferences(Collections.singletonList(SecurityReference.builder().scopes(new AuthorizationScope[0]).reference("token").build()))
                        // 声明作用域
                        .operationSelector(o -> o.requestMappingPattern().matches("/.*"))
                        .build()))
                .globalResponses(HttpMethod.GET, getGlobalResonseMessage())
                .globalResponses(HttpMethod.POST, getGlobalResonseMessage())
                // 接口文档的基本信息
                .apiInfo(apiInfo());
    }

    /**
     * 生成通用响应信息
     * @return
     */
    private List<Response> getGlobalResonseMessage() {
        List<Response> responseList = new ArrayList<>();
        for (ResponseCode c:ResponseCode.values()
             ) {
            responseList.add(new ResponseBuilder().code(c.getCode()).description(c.getDesc()).build());
        }

        return responseList;
    }
    /**
     * 接口文档详细信息
     *
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(applicationName)
                .description(applicationDescription)
                //.contact(new Contact("联系标题", "https://www.baidu.com", "510716143@qq.com"))
                .version(applicationVersion)
                .build();
    }

    private List<SecurityScheme> security() {
        ArrayList<SecurityScheme> apiKeys = new ArrayList<>();
        apiKeys.add(new ApiKey("token", "token", "header"));
        return apiKeys;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    public void setApplicationVersion(String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }

    public String getApplicationDescription() {
        return applicationDescription;
    }

    public void setApplicationDescription(String applicationDescription) {
        this.applicationDescription = applicationDescription;
    }
}
