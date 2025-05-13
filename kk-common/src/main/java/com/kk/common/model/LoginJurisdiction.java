package com.kk.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @Author：kk
 * @Date：2025/5/8 0:26
 */
@Data
public class LoginJurisdiction {

    /**
     * 资源权限列表
     */
    @ApiModelProperty("资源权限列表 例如 xxxx : 1 有效权限")
    private Map<String,String> jurisdictions;
    /**
     * 数据权限列表
     */
    @ApiModelProperty("数据权限列表 例如 xxxx : 1 有效权限")
    private Map<String,String> dataJurisdictions;
}
