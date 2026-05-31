package com.kk.common.auth;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
/**
 * created with IntelliJ IDEA.
 * description: sa-token RBAC 控制
 * auth: kk
 * date: 2026-05-27
 */


@Component // 必须交给 Spring 管理
public class LoginSaPermissionInterceptor implements StpInterface {



    // 构造器注入你的业务 Service
    public LoginSaPermissionInterceptor() {

    }

    /**
     * 返回指定账号 ID 所拥有的权限码集合
     * 比如返回：["user:add", "user:delete", "order:export"]
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 根据你的 RBAC 表结构，去数据库查询该用户拥有的所有权限标识
        LoginUserJurInfo loginUserJurInfo = LoginUtil.getLoginUserJurInfo();
        return loginUserJurInfo.getPermissionList()!=null ? loginUserJurInfo.getPermissionList() : new ArrayList<>();
    }

    /**
     * 返回指定账号 ID 所拥有的角色标识集合
     * 比如返回：["admin", "manager"]
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {

        // 根据你的 RBAC 表结构，去数据库查询该用户拥有的所有角色编码
        return new ArrayList<>(List.of("admin","manager"));
    }
}
