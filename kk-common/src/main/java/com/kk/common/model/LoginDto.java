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
     * 账号
     */
    @ApiModelProperty("账号")
    private String userId;
    /**
     * 用户名称
     */
    @ApiModelProperty("用户名称")
    private String userName;
    /**
     * 登录token
     */
    @ApiModelProperty("登录token")
    private String token;
    /**
     * 登录记录编号
     */
    @ApiModelProperty("登录记录编号")
    private String loginRecordId;
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
    /**
     * 用户等级（n：普通，vip:vip，svip:超级vip，lvip：永久vip）
     */
    @ApiModelProperty("用户等级（n：普通，vip:vip，svip:超级vip，lvip：永久vip）")
    private String userLevel;
    /**
     * 账号权限
     */
    @ApiModelProperty("账号权限")
    private LoginJurisdiction loginJurisdiction;
}
