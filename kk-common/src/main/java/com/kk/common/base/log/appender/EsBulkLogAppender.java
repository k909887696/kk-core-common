package com.kk.common.base.log.appender;

import com.kk.common.constant.SystemKey;
import com.kk.common.es.EsClientFactory;
import com.kk.common.es.EsTemplate;
import com.kk.common.utils.CommonUtil;
import com.kk.common.utils.DateUtil;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.message.MapMessage;

import javax.xml.crypto.Data;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Elasticsearch 批量日志 Appender
 * 队列缓存 + 满批次同步 flush，stop 时刷新剩余数据
 *
 * @Author: kk
 */
@Plugin(name = "EsBulkLogAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class EsBulkLogAppender extends AbstractAppender {

    private final String clientName;
    private final String indexName;
    private final String appName;
    private final String env;
    private final boolean includeMdc;
    private final int batchSize;

    private volatile EsTemplate esTemplate;
    private final BlockingQueue<Map<String, Object>> batchQueue;

    private final ScheduledExecutorService scheduler;

    private final long flushIntervalMs=2000;

    protected EsBulkLogAppender(String name, Filter filter, boolean ignoreExceptions,
                                String clientName, String indexName,
                                String appName, String env, boolean includeMdc,
                                int batchSize, int queueCapacity) {
        super(name, filter, null, ignoreExceptions, null);
        this.clientName = clientName != null ? clientName : SystemKey.LOGGING_STORE;
        this.indexName = indexName != null ? indexName : "kk-log-base";
        this.appName = appName != null ? appName : "unknown";
        this.env = env != null ? env : "dev";
        this.includeMdc = includeMdc;
        this.batchSize = batchSize > 0 ? batchSize : 5;
        this.batchQueue = new LinkedBlockingQueue<>(queueCapacity > 0 ? queueCapacity : 10000);
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "log-es-bulk-flush");
            t.setDaemon(true);
            return t;
        });
    }
    @Override
    public void start() {
        super.start();
        // 启动定时刷新任务
        scheduler.scheduleAtFixedRate(this::flushBatch, flushIntervalMs, flushIntervalMs, TimeUnit.MILLISECONDS);
    }
    @PluginFactory
    public static EsBulkLogAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginAttribute("loggingStoreIndex") String loggingStoreIndex,
            @PluginAttribute("loggingStoreName") String loggingStoreName,
            @PluginAttribute("loggingStoreEnv") String loggingStoreEnv,
            @PluginAttribute("includeMdc") String includeMdc,
            @PluginAttribute("batchSize") String batchSize,
            @PluginAttribute("queueCapacity") String queueCapacity,
            @PluginElement("Filter") Filter filter) {

        if (name == null) {
            //LOGGER.error("EsBulkLogAppender: name is required");
            return null;
        }

        boolean includeMdcBool = Boolean.parseBoolean(includeMdc);
        int batchSizeInt = Integer.parseInt(batchSize != null ? batchSize : "10");
        int queueCapacityInt = Integer.parseInt(queueCapacity != null ? queueCapacity : "10000");

        return new EsBulkLogAppender(name, filter, true,
                SystemKey.LOGGING_STORE, loggingStoreIndex, loggingStoreName, loggingStoreEnv, includeMdcBool,
                batchSizeInt, queueCapacityInt);
    }

    @Override
    public void append(LogEvent event) {
        try {
            Map<String, Object> logData = buildLogData(event);

            if (!batchQueue.offer(logData)) {
                LOGGER.warn("ES bulk queue is full, dropping log event");
                return;
            }

            // 达到批次大小立即 flush（同步）
            if (batchQueue.size() >= batchSize) {
                flushBatch();
            }

        } catch (Exception e) {
            LOGGER.error("EsBulkLogAppender append error", e);
        }
    }

    public void flushBatch() {
        if (batchQueue.isEmpty()) {
            return;
        }

        List<Map<String, Object>> batch = new ArrayList<>();
        Map<String, Object> logData;

        while ((logData = batchQueue.poll()) != null) {
            batch.add(logData);
        }

        if (batch.isEmpty()) {
            return;
        }

        try {
            if (esTemplate == null) {
                synchronized (this) {
                    if (esTemplate == null) {
                        esTemplate = EsTemplate.of(EsClientFactory.getClient(clientName), indexName);
                    }
                }
            }
            String dateStr = DateUtil.date2String(new Date(),DateUtil.PATTERN_STANDARD06W);
            esTemplate.bulkSaveOrUpdateMap(indexName+"-"+dateStr,batch);

        } catch (Exception e) {
            //LOGGER.error("EsBulkLogAppender flushBatch error, batch size={}", batch.size(), e);
            // 失败的日志重新放回队列（如果队列未满）
            for (Map<String, Object> item : batch) {
                if (!batchQueue.offer(item)) {
                    break;
                }
            }
        }
    }

    private Map<String, Object> buildLogData(LogEvent event) {
        Map<String, Object> logData = new HashMap<>();

        logData.put("@timestamp", Instant.ofEpochMilli(event.getTimeMillis()).toString());
        logData.put("storeName", appName);
        logData.put("storeEnv", env);
        logData.put("ip", getIpAddress());
        logData.put("hostName", getHostName());
        logData.put("level", event.getLevel().name());
        logData.put("logger", event.getLoggerName());
        logData.put("thread", event.getThreadName());
        logData.put("elapsed",DateUtil.elapsedTimeMillis(CommonUtil.getThreadStartTimeMillis()));

        if (event.getMessage() instanceof MapMessage) {
            MapMessage<?, ?> mapMsg = (MapMessage<?, ?>) event.getMessage();
            processMapMessage(mapMsg, logData);
        } else {
            logData.put(SystemKey.LOGGING_STORE_KEY, "main");
            logData.put("message", event.getMessage().getFormattedMessage());

        }

        if (event.getThrown() != null) {
            logData.put("exception", getStackTrace(event.getThrown()));
            logData.put("exceptionClass", event.getThrown().getClass().getName());
            logData.put("exceptionMessage", event.getThrown().getMessage());
        }

        if (includeMdc && event.getContextData() != null && !event.getContextData().isEmpty()) {
            Map<String, String> mdcData = new HashMap<>();
            event.getContextData().forEach((key, value) -> {
                if (value != null) {
                    mdcData.put(key, value.toString());
                }
            });
            logData.put("mdc", mdcData);
        }

        if (event.getSource() != null) {
            logData.put("className", event.getSource().getClassName());
            logData.put("methodName", event.getSource().getMethodName());
            logData.put("fileName", event.getSource().getFileName());
            logData.put("lineNumber", event.getSource().getLineNumber());
        }

        return logData;
    }

    private void processMapMessage(MapMessage<?, ?> mapMsg, Map<String, Object> logData) {
        mapMsg.forEach((key, value) -> {
            if (key != null && value != null) {
                logData.put(key.toString(), value.toString());
            }
        });
    }

    private String getStackTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        if (throwable.getCause() != null) {
            sb.append("Caused by: ");
            sb.append(getStackTrace(throwable.getCause()));
        }
        return sb.toString();
    }

    private String getHostName() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }

    private String getIpAddress() {
        try {
            return java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "unknown";
        }
    }

    @Override
    public void stop() {
        super.stop();
        // 刷新剩余日志
        flushBatch();
        LOGGER.info("EsBulkLogAppender [{}] stopped", getName());
    }
}
