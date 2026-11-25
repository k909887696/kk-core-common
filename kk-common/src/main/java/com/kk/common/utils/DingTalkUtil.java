package com.kk.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * 钉钉群机器人消息工具，支持文本、Markdown、@成员和可选加签。
 * <p>
 * webhook为包含access_token的完整机器人地址，secret从配置中读取，不应硬编码或记录到日志。
 * secret为空表示不加签，此时仍需满足机器人的关键词或IP白名单安全设置。
 * 发送失败抛出IllegalStateException，不自动重试，以免重复发送。
 * 每个机器人每分钟最多发送20条，调用方需自行控制频率。
 * <pre>
 * DingTalkUtil.sendText(webhook, secret, "监控报警：DAG执行失败");
 * DingTalkUtil.sendMarkdown(webhook, secret, "DAG告警", "### DAG执行失败\n请检查任务日志");
 * </pre>
 */
public final class DingTalkUtil {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    private DingTalkUtil() {
    }

    /** 发送不加签的文本消息。 */
    public static void sendText(String webhook, String content) {
        sendText(webhook, null, content);
    }

    /** 发送文本消息，secret为空时不加签。 */
    public static void sendText(String webhook, String secret, String content) {
        sendText(webhook, secret, content, null, null, false);
    }

    /**
     * 发送文本消息并指定@对象。
     * @param webhook 完整机器人地址
     * @param secret 可选加签密钥
     * @param content 文本内容；@成员时需在正文中包含对应的@手机号或@用户ID
     * @param atMobiles 可选的@手机号列表
     * @param atUserIds 可选的@用户ID列表
     * @param isAtAll 是否@所有人
     */
    public static void sendText(String webhook, String secret, String content,
                                List<String> atMobiles, List<String> atUserIds, boolean isAtAll) {
        JSONObject message = buildTextMessage(content, atMobiles, atUserIds, isAtAll);
        sendJson(buildWebhookUri(webhook, secret, new Date().getTime()), message);
    }

    /** 发送不加签的Markdown消息。 */
    public static void sendMarkdown(String webhook, String title, String text) {
        sendMarkdown(webhook, null, title, text);
    }

    /** 发送Markdown消息，secret为空时不加签。 */
    public static void sendMarkdown(String webhook, String secret, String title, String text) {
        sendMarkdown(webhook, secret, title, text, null, null, false);
    }

    /**
     * 发送Markdown消息并指定@对象；正文需包含对应的@手机号或@用户ID。
     * @param webhook 完整机器人地址
     * @param secret 可选加签密钥
     * @param title 消息标题
     * @param text Markdown正文
     * @param atMobiles 可选的@手机号列表
     * @param atUserIds 可选的@用户ID列表
     * @param isAtAll 是否@所有人
     */
    public static void sendMarkdown(String webhook, String secret, String title, String text,
                                    List<String> atMobiles, List<String> atUserIds, boolean isAtAll) {
        JSONObject message = buildMarkdownMessage(title, text, atMobiles, atUserIds, isAtAll);
        sendJson(buildWebhookUri(webhook, secret, new Date().getTime()), message);
    }

    static JSONObject buildTextMessage(String content, List<String> mobiles, List<String> userIds, boolean atAll) {
        requireText(content, "文本内容");
        JSONObject message = new JSONObject();
        message.put("msgtype", "text");
        JSONObject text = new JSONObject();
        text.put("content", content);
        message.put("text", text);
        message.put("at", buildAt(mobiles, userIds, atAll));
        return message;
    }

    static JSONObject buildMarkdownMessage(String title, String content, List<String> mobiles,
                                            List<String> userIds, boolean atAll) {
        requireText(title, "Markdown标题");
        requireText(content, "Markdown正文");
        JSONObject message = new JSONObject();
        message.put("msgtype", "markdown");
        JSONObject markdown = new JSONObject();
        markdown.put("title", title);
        markdown.put("text", content);
        message.put("markdown", markdown);
        message.put("at", buildAt(mobiles, userIds, atAll));
        return message;
    }

    private static JSONObject buildAt(List<String> mobiles, List<String> userIds, boolean atAll) {
        JSONObject at = new JSONObject();
        at.put("atMobiles", normalizeRecipients(mobiles));
        at.put("atUserIds", normalizeRecipients(userIds));
        at.put("isAtAll", atAll);
        return at;
    }

    private static List<String> normalizeRecipients(List<String> recipients) {
        return recipients == null ? List.of() : recipients.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim).distinct().toList();
    }

    static URI buildWebhookUri(String webhook, String secret, long timestamp) {
        requireText(webhook, "机器人地址");
        URI uri;
        try {
            uri = URI.create(webhook);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("机器人地址格式不正确");
        }
        if (!"https".equalsIgnoreCase(uri.getScheme())
                || !("oapi.dingtalk.com".equalsIgnoreCase(uri.getHost())
                    || "oapi.dingtalk.io".equalsIgnoreCase(uri.getHost()))
                || (uri.getPort() != -1 && uri.getPort() != 443)
                || !"/robot/send".equals(uri.getPath())
                || uri.getUserInfo() != null || uri.getFragment() != null || uri.getRawQuery() == null) {
            throw new IllegalArgumentException("请使用包含access_token的钉钉机器人HTTPS地址");
        }
        boolean hasToken = false;
        for (String parameter : uri.getRawQuery().split("&")) {
            String[] pair = parameter.split("=", 2);
            String name = decodeQuery(pair[0]);
            if ("timestamp".equals(name) || "sign".equals(name)) {
                throw new IllegalArgumentException("机器人地址不能预先包含timestamp或sign，请通过secret参数加签");
            }
            if ("access_token".equals(name)) {
                if (hasToken || pair.length < 2 || decodeQuery(pair[1]).isBlank()) {
                    throw new IllegalArgumentException("机器人access_token参数无效");
                }
                hasToken = true;
            }
        }
        if (!hasToken) {
            throw new IllegalArgumentException("机器人地址缺少access_token");
        }
        if (secret == null || secret.isBlank()) {
            return uri;
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signature = mac.doFinal((timestamp + "\n" + secret).getBytes(StandardCharsets.UTF_8));
            String sign = URLEncoder.encode(Base64.getEncoder().encodeToString(signature), StandardCharsets.UTF_8);
            return URI.create(webhook + "&timestamp=" + timestamp + "&sign=" + sign);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("钉钉机器人加签失败");
        }
    }

    private static String decodeQuery(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("机器人地址查询参数编码不正确");
        }
    }

    static void sendJson(URI uri, JSONObject message) {
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(REQUEST_TIMEOUT)
                .header("Content-Type", "application/json;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(message.toJSONString(), StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> response;
        try {
            response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("钉钉消息发送被中断");
        } catch (IOException e) {
            // 不附带底层异常，避免异常链泄露含access_token的请求地址
            throw new IllegalStateException("钉钉消息发送失败：网络异常或请求超时");
        }
        if (response.statusCode() != 200) {
            throw new IllegalStateException("钉钉消息发送失败：HTTP " + response.statusCode());
        }
        JSONObject result;
        try {
            result = JSON.parseObject(response.body());
        } catch (RuntimeException e) {
            throw new IllegalStateException("钉钉消息发送失败：响应不是有效JSON对象");
        }
        Object code = result == null ? null : result.get("errcode");
        if (!(code instanceof Number)) {
            throw new IllegalStateException("钉钉消息发送失败：响应缺少有效errcode");
        }
        if (!"0".equals(code.toString())) {
            // 不回显响应原文或errmsg，避免机器人服务返回敏感参数
            throw new IllegalStateException("钉钉消息发送失败：errcode=" + code);
        }
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + "不能为空");
        }
    }
}
