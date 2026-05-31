package com.kk.common.auth;

import lombok.Data;

import java.util.List;

/**
 * created with IntelliJ IDEA.
 * description: 登录用户权限信息
 * auth: kk
 * date: 2026-05-28
 */
@Data
public class LoginUserJurInfo {

    /**
     * 权限列表
     */
    private List<String> permissionList;
    /**
     * 数据权限列表
     */
    private List<String> dataPermissionList;
}
