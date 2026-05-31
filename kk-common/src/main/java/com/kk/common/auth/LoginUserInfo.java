package com.kk.common.auth;

import lombok.Data;

import java.util.Date;

/**
 * created with IntelliJ IDEA.
 * description: 登录信息
 * auth: kk
 * date: 2026-05-28
 */
@Data
public class LoginUserInfo {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String userName;
    /**
     * 用户类型
     */
    private String userType;
    /**
     * 登录时间
     */
    private Date loginTime;
    /**
     * 登录token
     */
    private String token;
    /**
     * 签名
     */
    private String signature;
    /**
     * 登录渠道(app,web)
     */
    private String loginChannel;
    /**
     * 登录IP
     */
    private String loginIp;
}
