package com.kk.common.utils;

import java.util.Calendar;

/**
 * DAG重试间隔计算工具类
 * 失败次数越多，间隔时间越长（指数退避策略）
 * 
 * @author kk
 * @since 2026-11-12
 */
public class DagRetryIntervalUtil {

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY_COUNT = 20;

    /**
     * 基础间隔时间（秒）
     */
    private static final long BASE_INTERVAL_SECONDS = 10;

    /**
     * 最大间隔时间（秒）- 30分钟
     */
    private static final long MAX_INTERVAL_SECONDS = 1800;

    /**
     * 计算下次执行时间
     * 
     * @param retryCount 当前重试次数（从1开始）
     * @return 下次执行的预执行时间
     */
    public static java.util.Date calculateNextRunTime(int retryCount) {
        if (retryCount <= 0) {
            retryCount = 1;
        }
        
        // 指数退避：baseInterval * 2^(retryCount-1)
        long intervalSeconds = BASE_INTERVAL_SECONDS * (long) Math.pow(2, retryCount - 1);
        
        // 限制最大间隔
        if (intervalSeconds > MAX_INTERVAL_SECONDS) {
            intervalSeconds = MAX_INTERVAL_SECONDS;
        }
        
        // 计算下次执行时间
        return DateUtil.addDate(new java.util.Date(), Calendar.SECOND, (int) intervalSeconds);
    }

    /**
     * 判断是否超过最大重试次数
     * 
     * @param retryCount 当前重试次数
     * @return true-已超过最大重试次数，false-未超过
     */
    public static boolean isExceedMaxRetry(int retryCount) {
        return retryCount >= MAX_RETRY_COUNT;
    }

    /**
     * 获取最大重试次数
     * 
     * @return 最大重试次数
     */
    public static int getMaxRetryCount() {
        return MAX_RETRY_COUNT;
    }

    /**
     * 获取间隔时间描述（用于日志）
     * 
     * @param retryCount 当前重试次数
     * @return 间隔时间描述
     */
    public static String getIntervalDescription(int retryCount) {
        if (retryCount <= 0) {
            retryCount = 1;
        }
        
        long intervalSeconds = BASE_INTERVAL_SECONDS * (long) Math.pow(2, retryCount - 1);
        if (intervalSeconds > MAX_INTERVAL_SECONDS) {
            intervalSeconds = MAX_INTERVAL_SECONDS;
        }
        
        if (intervalSeconds < 60) {
            return intervalSeconds + "秒";
        } else if (intervalSeconds < 3600) {
            return (intervalSeconds / 60) + "分钟";
        } else {
            return (intervalSeconds / 3600) + "小时";
        }
    }
}
