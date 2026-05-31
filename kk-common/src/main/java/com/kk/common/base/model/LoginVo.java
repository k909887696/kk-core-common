package com.kk.common.base.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * created with IntelliJ IDEA.
 * description: 登录实体
 * auth: kk
 * date: 2026-05-27
 */
@Data
public class LoginVo {
    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String userId;
    /**
     * 密码
     */
    @Schema(description = "密码")
    private String password;
    /**
     * 验证码
     */
    @Schema(description = "验证码")
    private String code;
}
