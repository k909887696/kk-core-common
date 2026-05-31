package com.kk.common.base.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * created with IntelliJ IDEA.
 * description:
 * auth: kk
 * date: 2026-05-27
 */
@Data
public class LoginRes {
    /**
     * token
     */
    @Schema(description = "token")
    private String token;
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private String userId;
}
