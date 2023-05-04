package com.kk.common.utils;

import com.kk.common.constant.PasswordValidResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author：kk 2022/11/21  10:15
 * 正则工具类
 */
public class RegUtil {

    public static final String illegalCharReg = "[^～@#_*%/\\.+:=0-9a-zA-Z]+"; //禁用非法字符
    public static final String numberReg = "[0-9]+";                           //纯数字
    public static final String charReg = "[a-zA-Z]+";                          //纯字母
    public static final String legalSpecialCharReg = "[～@#_*%/\\.+:=]+";      //特殊字符
    public static final String repeatsCharReg = "(\\w)\\1{5}";                 //禁用最多连续重复字符6个 如：aaaaaa、bbbbbb、111111等
    public static final String simpleCharReg ="(1234|abcd|qwer|4321)";         //禁用简单字符：密码包含1234、abcd、qwer、4321提示
    /**
     * 校验密码复杂度
     * @param password 密码
     * @return 校验结果
     */
    public static PasswordValidResponse passwordValid(String password)
    {
        PasswordValidResponse response = new PasswordValidResponse();
        response.setCode("E");
        if(StringUtils.isBlank(password))
        {
            response.setDesc("密码为空！");
            return response;
        }
        if(password.length() < 8 || password.length() >20)
        {
            response.setDesc("请输入8～20位字符串");
            return response;
        }
        Matcher m_repeatsCharReg = Pattern.compile(repeatsCharReg).matcher(password);
        if(m_repeatsCharReg.find()){
            response.setDesc("密码包含超过6位的连续重复字符，为了您的帐号安全，请重新填写");
            return response;
        }
        Matcher m_illegalCharReg = Pattern.compile(illegalCharReg).matcher(password);
        if(m_illegalCharReg.find()){
            response.setDesc("密码包含非法字符，仅支持特殊字符：～@ # _ * % / . + : ; =");
            return response;
        }
        Matcher m_simpleCharReg = Pattern.compile(simpleCharReg).matcher(password);
        if(m_simpleCharReg.find()){
            response.setDesc(String.format("请勿使用{}等简易密码",m_simpleCharReg.group()));
            return response;
        }
        Matcher m_numberReg = Pattern.compile(numberReg).matcher(password);
        Matcher m_charReg = Pattern.compile(charReg).matcher(password);
        Matcher m_legalSpecialCharReg = Pattern.compile(legalSpecialCharReg).matcher(password);
        boolean f_numberReg = m_numberReg.find();
        boolean f_charReg = m_charReg.find();
        boolean f_legalSpecialCharReg = m_legalSpecialCharReg.find();
        if((!f_numberReg && !f_charReg)
                || (!f_legalSpecialCharReg && !f_charReg)
                || (!f_numberReg && !f_legalSpecialCharReg)
        ){
            response.setDesc("特殊字符、大小写字母、数字,必须包含两种");
            return response;
        }
        if(f_numberReg && f_charReg && f_legalSpecialCharReg)
        {
            response.setCode("S");
        }else {
            response.setCode("M");
        }
        response.setDesc("");
        return response;
    }
}
