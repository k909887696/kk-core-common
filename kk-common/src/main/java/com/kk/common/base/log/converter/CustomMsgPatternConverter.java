package com.kk.common.base.log.converter;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.message.MapMessage;
import org.apache.logging.log4j.message.Message;
/**
 * created with IntelliJ IDEA.
 * description:
 * auth: kk
 * date: 2026-05-14
 */
@Plugin(name = "CustomMsgPatternConverter", category = "Converter")
@ConverterKeys({"cmsg"})
public class CustomMsgPatternConverter extends LogEventPatternConverter {

    protected CustomMsgPatternConverter(String name, String style) {
        super(name, style);
    }

    @PluginFactory
    public static CustomMsgPatternConverter newInstance() {
        return new CustomMsgPatternConverter("cmsg", "cmsg");
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo) {
        Message msg = event.getMessage();

        // 如果是 MapMessage，且包含 "message" 键，只输出这个值
        if (msg instanceof MapMessage) {
            MapMessage<?, ?> mapMsg = (MapMessage<?, ?>) msg;
            Object messageValue = mapMsg.get("message");
            if (messageValue != null) {
                toAppendTo.append(messageValue);
                return;
            }
        }

        // 否则（普通日志或没有 message 键），输出原始 formatted message
        toAppendTo.append(msg.getFormattedMessage());
    }
}
