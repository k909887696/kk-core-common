package com.kk.common.es;

import com.kk.common.constant.SystemKey;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * created with IntelliJ IDEA.
 * description: Elasticsearch 7.10.1 客户端工厂
 * auth: kk
 * date: 2026-05-18
 */
public class EsClientFactory {

    private static final Map<String, RestHighLevelClient> CLIENT_MAP = new ConcurrentHashMap<>();
    private static final Map<String, BusinessEsProperties.EsNode> NODE_CONFIG_MAP = new ConcurrentHashMap<>();

    /**
     * 初始化客户端（从配置属性加载）
     */
    public static void init(BusinessEsProperties properties) {
        if (properties.getNodes() == null || properties.getNodes().isEmpty()) {
            return;
        }
        for (Map.Entry<String, BusinessEsProperties.EsNode> entry : properties.getNodes().entrySet()) {
            String name = entry.getKey();
            BusinessEsProperties.EsNode node = entry.getValue();
            RestHighLevelClient client = createClient(node);
            CLIENT_MAP.put(name, client);
            NODE_CONFIG_MAP.put(name, node);
        }
    }

    /**
     * 动态添加客户端
     */
    public static void addClient(String name, BusinessEsProperties.EsNode node) {
        RestHighLevelClient client = createClient(node);
        CLIENT_MAP.put(name, client);
        NODE_CONFIG_MAP.put(name, node);
    }

    /**
     * 动态添加客户端（直接传入 RestHighLevelClient）
     */
    public static void addClient(String name, RestHighLevelClient client) {
        CLIENT_MAP.put(name, client);
    }

    /**
     * 获取客户端
     */
    public static RestHighLevelClient getClient(String name) {
        RestHighLevelClient client = CLIENT_MAP.get(name);
        if (client == null) {
            throw new IllegalArgumentException("未找到名为 [" + name + "] 的 Elasticsearch 客户端");
        }
        return client;
    }

    /**
     * 获取默认客户端
     */
    public static RestHighLevelClient getClient() {
        if (CLIENT_MAP.isEmpty()) {
            throw new IllegalStateException("没有可用的 Elasticsearch 客户端");
        }
        return CLIENT_MAP.get(SystemKey.ES_DEFAULT_CLIENT);
    }

    /**
     * 检查客户端是否存在
     */
    public static boolean containsClient(String name) {
        return CLIENT_MAP.containsKey(name);
    }

    /**
     * 移除客户端
     */
    public static void removeClient(String name) {
        RestHighLevelClient client = CLIENT_MAP.remove(name);
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                // ignore
            }
        }
        NODE_CONFIG_MAP.remove(name);
    }

    /**
     * 获取所有客户端名称
     */
    public static java.util.Set<String> getClientNames() {
        return CLIENT_MAP.keySet();
    }

    /**
     * 关闭所有客户端
     */
    public static void closeAll() {
        for (RestHighLevelClient client : CLIENT_MAP.values()) {
            try {
                client.close();
            } catch (IOException e) {
                // ignore
            }
        }
        CLIENT_MAP.clear();
        NODE_CONFIG_MAP.clear();
    }

    /**
     * 创建 ES 客户端
     */
    private static RestHighLevelClient createClient(BusinessEsProperties.EsNode node) {
        HttpHost httpHost = HttpHost.create(node.getHost());
        RestClientBuilder builder = RestClient.builder(httpHost);

        // 设置超时
        builder.setRequestConfigCallback(requestConfigBuilder ->
                requestConfigBuilder
                        .setConnectTimeout(node.getConnectTimeout())
                        .setSocketTimeout(node.getSocketTimeout())
                        .setConnectionRequestTimeout(node.getConnectionRequestTimeout())
        );

        // 认证
        if (node.getUsername() != null && !node.getUsername().isEmpty() && node.getPassword() != null) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(node.getUsername(), node.getPassword()));
            builder.setHttpClientConfigCallback(httpClientBuilder ->
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        }

        return new RestHighLevelClient(builder);
    }
}
