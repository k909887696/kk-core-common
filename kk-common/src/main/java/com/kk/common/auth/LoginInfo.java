package com.kk.common.auth;

import lombok.Data;

/**
 * created with IntelliJ IDEA.
 * description: 登录信息
 * auth: kk
 * date: 2026-05-28
 */
@Data
public class LoginInfo {

    /**
     * 登录用户信息
     */
    private LoginUserInfo loginUserInfo;
    /**
     * 登录权限信息
     */
    private LoginUserJurInfo loginUserJurInfo;


}
