package com.kk.common.auth;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.kk.common.constant.SystemKey;

import java.util.Date;

/**
 * created with IntelliJ IDEA.
 * description: 登录认证工具类
 * auth: kk
 * date: 2026-05-28
 */
public class LoginUtil {

    /**
     * 登录
     * @param loginInfo
     */
    public static LoginInfo login(LoginInfo loginInfo) {
        StpUtil.login(loginInfo.getLoginUserInfo().getUserId());
        SaTokenInfo saTokenInfo = StpUtil.getTokenInfo();
        loginInfo.getLoginUserInfo().setToken(saTokenInfo.getTokenValue());
        loginInfo.getLoginUserInfo().setLoginTime(new Date());
        setLoginUserInfo(loginInfo.getLoginUserInfo());
        setLoginUserJurInfo(loginInfo.getLoginUserJurInfo());
        return loginInfo;
    }

    /**
     * 获取登录用户信息
     * @return
     */
    public static LoginUserInfo getLoginUserInfo() {
        LoginUserInfo loginUserInfo = (LoginUserInfo) StpUtil.getTokenSession().get(SystemKey.LoginUserCacheKey);
        return loginUserInfo;
    }

    /**
     * 设置登录用户信息
     * @param loginUserInfo
     */
    public static void setLoginUserInfo(LoginUserInfo loginUserInfo) {
        StpUtil.getTokenSession().set(SystemKey.LoginUserCacheKey, loginUserInfo);
    }

    /**
     * 移除登录用户信息
     */
    public static void removeLoginUserInfo() {
        StpUtil.getTokenSession().delete(SystemKey.LoginUserCacheKey);
    }

    /**
     * 设置登录用户权限信息
     * @param loginUserJurInfo
     */
    public static void setLoginUserJurInfo(LoginUserJurInfo loginUserJurInfo) {
        StpUtil.getTokenSession().set(SystemKey.LoginUserJurisdictionCacheKey, loginUserJurInfo);
    }

    /**
     * 获取登录用户权限信息
     * @return
     */
    public static LoginUserJurInfo getLoginUserJurInfo() {
        LoginUserJurInfo loginUserJurInfo = (LoginUserJurInfo) StpUtil.getTokenSession().get(SystemKey.LoginUserJurisdictionCacheKey);
        return loginUserJurInfo;
    }
}
