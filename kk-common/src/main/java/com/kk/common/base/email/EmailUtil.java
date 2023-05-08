package com.kk.common.base.email;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * @Author: kk
 * @Date: 2021/12/28 17:09
 */
@Component
public class EmailUtil {

    @Resource
    private JavaMailSenderImpl javaMailSenderImpl;

    public void sendMimeMail(EmailSendMsg sendMsg) {
        MimeMessage mimeMessage = javaMailSenderImpl.createMimeMessage();
        try {
            // 开启文件上传
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            // 设置文件主题
            mimeMessageHelper.setSubject(sendMsg.getSubject());
            // 设置文件内容 第二个参数设置是否支持html
            mimeMessageHelper.setText(sendMsg.getText(), true);
            // 设置发送到的邮箱
            mimeMessageHelper.setTo(sendMsg.getTo().toArray(new String[sendMsg.getTo().size()]));
            // 设置发送人和配置文件中邮箱一致
            mimeMessageHelper.setFrom(sendMsg.getFrom());
            // 上传附件
            // mimeMessageHelper.addAttachment("", new File(""));
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        javaMailSenderImpl.send(mimeMessage);
    }
}
