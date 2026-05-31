package com.kk.common.web.model;




import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * @Author: kk
 * @Date: 2021/11/17 14:17
 */
@Schema(name = "ApiResult",description = "api统一返回类")
public class ApiResult<T> implements Serializable {
    public static final String OK = "200";
    public static final String ERR = "-1";
    public static final ApiResult SUCCESS = new ApiResult("200", "操作成功！");
    @Schema(

            description = "状态码，除了\"200\"表示放回正确结果，其余都是异常状态"
    )
    private String code;
    @Schema(

            description = "返回结果的描述"
    )
    private String desc;
    @Schema(

            description = "接口返回的数据"
    )
    private T data;

    public static ApiResult getFailResult(String message) {
        return new ApiResult(ERR, message);
    }

    public static ApiResult getSuccessResult(String message) {
        return new ApiResult(OK, message);
    }

    public static ApiResult getSuccessResult(Object data, String message) {
        return new ApiResult(OK, message, data);
    }

    public ApiResult() {
        this.code = OK;
        this.desc = SUCCESS.desc;
    }

    public ApiResult(String code, String message) {
        this.code = code;
        this.desc = message;
    }

    public ApiResult(String code, String message, T data) {
        this.code = ERR;
        this.desc = "";
        this.code = code;
        this.desc = message;
        this.data = data;
    }

    public ApiResult(T data) {
        this(OK, "操作成功！", data);
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj == this) {
            return true;
        } else {
            ApiResult ret = (ApiResult)obj;
            return this.code == ret.getCode();
        }
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
