package com.kk.common.utils;

import com.kk.common.constant.SystemKey;
import com.kk.common.es.EsTemplate;
import com.kk.common.trace.TraceData;
import com.xxl.job.core.context.XxlJobHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.logging.log4j.message.StringMapMessage;

import java.util.Arrays;

/**
 * created with IntelliJ IDEA.
 * description: 日志公共类
 * auth: kk
 * date: 2026-05-11
 */
public class LogUtil {

    /**
     * 输出带 logKey 字段的日志
     *
     * @param logger  目标 Logger
     * @param level   日志级别：info/warn/error/debug
     * @param message 日志正文
     * @param logKey 关键字串，格式：关键字1;关键字2;关键字3
     * @param params  其他自定义字段（可选）
     */
    public static void log(Logger logger, String logKey,String level, String message,  Object... params) {
        String seqNo = TraceData.traceId.get();
       if(StringUtils.isBlank(seqNo))//内部接口、或者调度等没有请求链路时，生成一个全局唯一ID
       {
           seqNo = CommonUtil.generateUUID();
           TraceData.traceId.set(seqNo);
       }
        logKey = seqNo + ";" + logKey;
        //paramsList[params.length] = logKey;
        // ========== 关键修改：使用 Log4j2 内置格式化 ==========
        String formattedMessage = ParameterizedMessage.format(message, params);
        StringMapMessage msg = new StringMapMessage()
                .with("message", formattedMessage)
                .with(SystemKey.LOGGING_STORE_KEY, logKey);

        // 追加其他业务字段
        switch (level.toLowerCase()) {
            case "info" -> logger.info(msg);
            case "warn" -> logger.warn(msg);
            case "error" -> logger.error(msg);
            case "debug" -> logger.debug(msg);
            default -> logger.info(msg);
        }
    }

    /**
     * 输出带 logKey 字段的日志
     *
     * @param logger  目标 Logger
     * @param message 日志正文
     * @param logKey 关键字串，格式：关键字1;关键字2;关键字3
     * @param params  其他自定义字段（可选）
     */
    public static void info(Logger logger,String logKey, String message, final Object... params) {
        log(logger, logKey,"info", message,  params);
    }
    /**
     * 输出带 logKey 字段的日志
     *
     * @param logger  目标 Logger
     * @param message 日志正文
     * @param logKey 关键字串，格式：关键字1;关键字2;关键字3
     * @param params  其他自定义字段（可选）
     */
    public static void error(Logger logger,String logKey, String message, final Object... params) {
        log(logger, logKey,"error", message,  params);
    }

    /**
     * 整合xxl-job 与 log4j
     * @param appendLogPattern
     * @param appendLogArguments
     */
    public static void logInfoXxlAnd4j(Logger logger,String logkey,String appendLogPattern, Object... appendLogArguments)
    {
        XxlJobHelper.log(appendLogPattern,appendLogArguments);
        LogUtil.info(logger,logkey,appendLogPattern,appendLogArguments);
    }
    public static void logErrorXxlAnd4j(Logger logger,String logkey,String appendLogPattern, Object... appendLogArguments)
    {
        XxlJobHelper.log(appendLogPattern,appendLogArguments);
        LogUtil.error(logger,logkey,appendLogPattern,appendLogArguments);
    }
}
