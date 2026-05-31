package com.kk.common.base.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * created with IntelliJ IDEA.
 * description:
 * auth: kk
 * date: 2026-05-22
 */
@Configuration
@Data
public class BaseConfing {
    /**
     * 日志存储配置 文件地址
     */
    @Value("${logging.file.path:./logs}")
    private String loggingFilePath;
    /**
     * 日志存储配置 写入远程服务ip（例如es：http://127.0.0.1:9200）
     */
    @Value("${logging.store.host:}")
    private String loggingStoreHost;

    /**
     * 日志存储配置 写入远程服务索引名称
     */
    @Value("${logging.store.index:kk-log-base}")
    private String loggingStoreIndex;
    /**
     * 日志存储配置 写入远程服务用户名
     */
    @Value("${logging.store.username:}")
    private String loggingStoreUserName;
    /**
     * 日志存储配置 写入远程服务密码
     */
    @Value("${logging.store.password:}")
    private String loggingStorePassword;
    /**
     * 日志存储配置 写入远程服务应用名称（一般为本服务的名称）
     */
    @Value("${logging.store.name:}")
    private String loggingStoreName;
    /**
     * 日志存储配置 写入远程服务环境名称（例如：dev、test、pre、prod）
     */
    @Value("${logging.store.env:}")
    private String loggingStoreEnv;
}
