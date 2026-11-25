package com.kk.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpServer;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/** 仅访问本机模拟服务，不向真实钉钉群发送消息。 */
public class DingTalkUtilTest {

    private static final String WEBHOOK = "https://oapi.dingtalk.com/robot/send?access_token=test-token";

    @Test
    public void textMessagePreservesChineseAndNormalizesRecipients() {
        String content = "监控报警：\"执行失败\"\n@13800000000";
        JSONObject message = DingTalkUtil.buildTextMessage(content,
                Arrays.asList(null, "", " 13800000000 ", "13800000000"), List.of("user-1"), false);
        JSONObject decoded = JSON.parseObject(message.toJSONString());
        assertEquals("text", decoded.getString("msgtype"));
        assertEquals(content, decoded.getJSONObject("text").getString("content"));
        assertEquals(List.of("13800000000"), decoded.getJSONObject("at").getJSONArray("atMobiles"));
        assertEquals(List.of("user-1"), decoded.getJSONObject("at").getJSONArray("atUserIds"));
        assertFalse(decoded.getJSONObject("at").getBooleanValue("isAtAll"));
    }

    @Test
    public void markdownSupportsAtAllAndMissingRecipients() {
        JSONObject message = DingTalkUtil.buildMarkdownMessage("告警", "### 失败\n详情", null, null, true);
        assertEquals("markdown", message.getString("msgtype"));
        assertEquals("告警", message.getJSONObject("markdown").getString("title"));
        assertEquals("### 失败\n详情", message.getJSONObject("markdown").getString("text"));
        assertTrue(message.getJSONObject("at").getBooleanValue("isAtAll"));
        assertEquals(List.of(), message.getJSONObject("at").get("atMobiles"));
        assertEquals(List.of(), message.getJSONObject("at").get("atUserIds"));
    }

    @Test
    public void signingMatchesIndependentHmacVector() {
        URI signed = DingTalkUtil.buildWebhookUri(WEBHOOK, "test-signing-key", 1600000000000L);
        assertEquals(WEBHOOK + "&timestamp=1600000000000&sign=xgGXUJmfMSbR2D14wCNyDDLa69Pf1iaHi8HffV3wUmE%3D",
                signed.toString());
        assertFalse(signed.equals(DingTalkUtil.buildWebhookUri(WEBHOOK, "test-signing-key", 1600000000001L)));
    }

    @Test
    public void missingSecretDoesNotAppendSignature() {
        for (String secret : Arrays.asList(null, "", " ")) {
            assertEquals(WEBHOOK, DingTalkUtil.buildWebhookUri(WEBHOOK, secret, 1600000000000L).toString());
        }
    }

    @Test
    public void rejectsInvalidWebhookWithoutExposingCredentials() {
        for (String webhook : Arrays.asList(null, "", " ", "not a url?access_token=test-token",
                "http://oapi.dingtalk.com/robot/send?access_token=test-token",
                "https://example.com/robot/send?access_token=test-token",
                "https://oapi.dingtalk.com/robot/send", WEBHOOK + "#fragment",
                "https://oapi.dingtalk.com/robot/send?access_token=",
                WEBHOOK + "&access_token=other", WEBHOOK + "&sign=old", WEBHOOK + "&timestamp=1")) {
            try {
                DingTalkUtil.buildWebhookUri(webhook, null, 1600000000000L);
                fail("无效地址应被拒绝");
            } catch (IllegalArgumentException e) {
                assertFalse(e.getMessage().contains("test-token"));
                assertNull(e.getCause());
            }
        }
    }

    @Test
    public void blankMessageIsRejectedBeforeSending() {
        for (String content : Arrays.asList(null, "", " \n")) {
            try {
                DingTalkUtil.sendText(WEBHOOK, content);
                fail("空文本应被拒绝");
            } catch (IllegalArgumentException expected) {
                assertTrue(expected.getMessage().contains("文本内容"));
            }
            try {
                DingTalkUtil.sendMarkdown(WEBHOOK, content, "正文");
                fail("空标题应被拒绝");
            } catch (IllegalArgumentException expected) {
                assertTrue(expected.getMessage().contains("Markdown标题"));
            }
            try {
                DingTalkUtil.sendMarkdown(WEBHOOK, "标题", content);
                fail("空正文应被拒绝");
            } catch (IllegalArgumentException expected) {
                assertTrue(expected.getMessage().contains("Markdown正文"));
            }
        }
    }

    @Test
    public void sendsUtf8JsonPostAndChecksSuccess() throws Exception {
        AtomicReference<String> method = new AtomicReference<>();
        AtomicReference<String> contentType = new AtomicReference<>();
        AtomicReference<String> received = new AtomicReference<>();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/robot/send", exchange -> {
            method.set(exchange.getRequestMethod());
            contentType.set(exchange.getRequestHeaders().getFirst("Content-Type"));
            received.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            byte[] body = "{\"errcode\":0,\"errmsg\":\"ok\"}".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            try (var output = exchange.getResponseBody()) {
                output.write(body);
            }
        });
        server.start();
        try {
            DingTalkUtil.sendJson(localUri(server), DingTalkUtil.buildTextMessage("中文告警", null, null, false));
            assertEquals("POST", method.get());
            assertEquals("application/json;charset=UTF-8", contentType.get());
            assertEquals("中文告警", JSON.parseObject(received.get()).getJSONObject("text").getString("content"));
        } finally {
            server.stop(0);
        }
    }

    @Test
    public void httpFailureIsNotRetriedOrReportedAsSuccess() throws Exception {
        assertRejectedResponse(500, "test-token", "HTTP 500");
        assertRejectedResponse(302, "test-token", "HTTP 302");
    }

    @Test
    public void businessFailureIsNotRetriedOrReportedAsSuccess() throws Exception {
        assertRejectedResponse(200, "{\"errcode\":310000,\"errmsg\":\"test-token\"}", "errcode=310000");
    }

    @Test
    public void malformedAndMissingErrorCodesAreRejected() throws Exception {
        for (String body : List.of("not-json", "", "null", "{}", "[]", "{\"errcode\":null}", "{\"errcode\":\"0\"}")) {
            assertRejectedResponse(200, body, "钉钉消息发送失败");
        }
    }

    @Test
    public void interruptionIsPreserved() {
        Thread.currentThread().interrupt();
        try {
            DingTalkUtil.sendJson(URI.create("http://127.0.0.1:1/robot/send"),
                    DingTalkUtil.buildTextMessage("测试中断", null, null, false));
            fail("线程中断应终止发送");
        } catch (IllegalStateException e) {
            assertTrue(Thread.currentThread().isInterrupted());
            assertTrue(e.getMessage().contains("中断"));
        } finally {
            Thread.interrupted();
        }
    }

    private void assertRejectedResponse(int status, String body, String expectedError) throws Exception {
        AtomicInteger calls = new AtomicInteger();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/robot/send", exchange -> {
            calls.incrementAndGet();
            exchange.getRequestBody().readAllBytes();
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, bytes.length == 0 ? -1 : bytes.length);
            try (var output = exchange.getResponseBody()) {
                output.write(bytes);
            }
        });
        server.start();
        try {
            DingTalkUtil.sendJson(localUri(server), DingTalkUtil.buildTextMessage("测试", null, null, false));
            fail("错误响应应抛出异常");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage(), e.getMessage().contains(expectedError));
            assertFalse(e.getMessage().contains("test-token"));
            assertNull(e.getCause());
            assertEquals(1, calls.get());
        } finally {
            server.stop(0);
        }
    }

    private URI localUri(HttpServer server) {
        return URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/robot/send");
    }
}
