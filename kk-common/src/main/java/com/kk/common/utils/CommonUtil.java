package com.kk.common.utils;

import com.kk.common.trace.TraceData;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

/**
 * created with IntelliJ IDEA.
 * description:
 * auth: kk
 * date: 2026-05-09
 */
public class CommonUtil {

    /**
     * 获取当前线程的开始时间
     * @return
     */
    public static long getThreadStartTimeMillis(){

        long startTime = TraceData.seqStart!=null && TraceData.seqStart.get()!=null? TraceData.seqStart.get():System.currentTimeMillis();
        return startTime;
    }

    /**
     * 根据url获取请求方法名和控制器
     * @param url
     * @return
     */
    public static String getRequestUrlMethod(String  url) {
        String methodName = "";
        if (!StringUtils.isEmpty(url)) {
            String[] uris = url.split("/");
            methodName = uris[uris.length - 2] +";"+ uris[uris.length - 1];
        }
        return methodName;
    }

    /**
     * 生成UUID
     * 格式：9C0202158439432CA23973321FA6D806
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().toUpperCase(Locale.ROOT).replace("-","");
    }

    /**
     * 计算字符串占用的字节大小（KB）
     * @param str 待计算的字符串
     * @return 字符串字节大小，单位 KB，向下取整
     */
    public static long getStringSizeInKB(String str) {
        if (str == null || str.isEmpty()) {
            return 0L;
        }
        return str.getBytes(StandardCharsets.UTF_8).length / 1024L;
    }

    /**
     * 请求字符串大小级别
     * @param sizeInKB
     * @return
     */
    public static String getStringSizeLevel(long sizeInKB)
    {
        if (sizeInKB <500 && sizeInKB>= 200)
        {
            return "B";
        }
        else if(sizeInKB <1024 && sizeInKB>=500)
        {
            return "SB";
        }
        else if(sizeInKB <2048 && sizeInKB>=1024)
        {
            return "SSB";
        }
        else if(sizeInKB>=2048)
        {
            return "SSSB";
        }else {
            return "";
        }
    }
}
