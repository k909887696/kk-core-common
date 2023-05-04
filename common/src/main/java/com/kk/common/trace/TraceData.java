package com.kk.common.trace;

/**
 * @Author: kk
 * @Date: 2021/11/19 17:14
 */
public class TraceData {
    public static ThreadLocal<String> traceId = new ThreadLocal();
    public static ThreadLocal<Long> seqStart = new ThreadLocal();

    public TraceData() {
    }
}
