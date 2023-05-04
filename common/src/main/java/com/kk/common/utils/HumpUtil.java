package com.kk.common.utils;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: kk
 * @Date: 2021/12/11 15:27
 */
public class HumpUtil {
    private static Pattern linePattern = Pattern.compile("_(\\w)");
    /**
     * 下划线转驼峰
     * */
    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    /**
     * 驼峰转下划线,(小写)
     * */
    public static String humpToLineLowerCase(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 驼峰转下划线,（大写）
     * */
    public static String humpToLineUpperCase(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}
