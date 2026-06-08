package com.kk.common.base.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * created with IntelliJ IDEA.
 * description: 登录权限信息
 * auth: kk
 * date: 2026-06-08
 */
@Data
public class LoginPermissionVo {
    /**
     * 权限列表
     */
    @Schema(description = "权限列表")
    private List<String> permissionList;
}
