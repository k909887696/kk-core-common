package com.kk.common.utils;

/**
 * 重试间隔计算工具类
 */
public class RetryTimeUtil {

    /**
     * 根据重试次数获取重试间隔
     *
     * @param retryCount 重试次数，不允许为null
     * @return 重试间隔（秒），未匹配的次数返回1200秒
     */
    public static int mqRetryTime(Integer retryCount) {
        int result = 10;
        switch (retryCount) {
            case 0:
                result = 1;
                break;
            case 1:
                result = 5;
                break;
            case 2:
                result = 10;
                break;
            case 3:
                result = 10;
                break;
            case 4:
                result = 10;
                break;
            case 5:
                result = 20;
                break;
            case 6:
                result = 20;
                break;
            case 7:
                result = 60;
                break;
            case 8:
                result = 60;
                break;
            case 9:
                result = 200;
                break;
            case 10:
                result = 200;
                break;
            case 11:
                result = 600;
                break;
            case 12:
                result = 600;
                break;
            case 13:
                result = 600;
                break;
            default:
                result = 1200;
                break;
        }
        return result;
    }
}
