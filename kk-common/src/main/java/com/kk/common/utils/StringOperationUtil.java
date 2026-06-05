package com.kk.common.utils;

/**
 * created with IntelliJ IDEA.
 * description: 字符串操作工具类
 * auth: kk
 * date: 2026-06-02
 */
public class StringOperationUtil {
    /**
     * 判断字符串是否为空
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 首字母大写
     * @param str
     * @return
     */
    public static String  capFirst(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 首字母小写
     * @param str
     * @return
     */
    public static String  unCapFirst(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
}
