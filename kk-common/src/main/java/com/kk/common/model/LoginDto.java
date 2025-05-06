package com.kk.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author：kk
 * @Date：2025/4/23 22:15
 */
@Data
@ApiModel(value = "登录信息", description = "登录信息")
public class LoginDto implements Serializable {
    /**
     * 用户名
     */
    @ApiModelProperty("用户名")
    private String userName;
    /**
     * 登录token
     */
    @ApiModelProperty("登录token")
    private String token;
    /**
     * 登录时间
     */
    @ApiModelProperty("登录时间")
    private Date loginTime;
    /**
     * 过期时间
     */
    @ApiModelProperty("过期时间")
    private Date expireTime;
}
