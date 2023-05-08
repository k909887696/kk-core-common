package com.kk.common.constant;

/**
 * @author kk
 * 密码复杂度校验返回实体
 */
public class PasswordValidResponse {

    private static final long serialVersionUID = 1L;
    /**
     * 提示信息
     */
    private String desc;
    /**
     * 复杂等级（L:简单，M：中等，S:强，E：异常（不符合规则））
     */
    private String code;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
