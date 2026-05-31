package com.kk.common.utils;

/**
 * created with IntelliJ IDEA.
 * description: 对象操作类
 * auth: kk
 * date: 2026-05-11
 */
public class BeanUtil {

    /**
     * 拷贝对象属性
     * @param source
     * @param target
     */
    public static void copyProperties(Object source, Object target) {
        org.springframework.beans.BeanUtils.copyProperties(source, target);
    }
}
