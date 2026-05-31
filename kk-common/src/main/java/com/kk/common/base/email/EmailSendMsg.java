package com.kk.common.base.email;

import lombok.Data;

import java.util.List;

/**
 * @Author: kk
 * @Date: 2021/12/28 17:10
 */
@Data
public class EmailSendMsg {

    /**
     * 主题
     */
    private String subject;
    /**
     * 内容
     */
    private String text;
    /**
     * 接收人集合
     */
    private List<String> to;
    /**
     * 抄送人集合
     */
    private List<String> cc;
    /**
     * 发送人
     */
    private String from;

}
