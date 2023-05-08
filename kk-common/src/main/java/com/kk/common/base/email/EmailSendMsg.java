package com.kk.common.base.email;

import java.util.List;

/**
 * @Author: kk
 * @Date: 2021/12/28 17:10
 */
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
     * 发送人
     */
    private String from;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
