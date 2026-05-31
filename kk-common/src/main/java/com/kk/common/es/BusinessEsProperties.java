package com.kk.common.es;


import com.kk.common.constant.SystemKey;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Elasticsearch 多数据源配置属性
 */
@Component
@ConfigurationProperties("business-es")
public class BusinessEsProperties {

    /**
     * 默认数据源名称
     */
    private String primary = SystemKey.ES_DEFAULT_CLIENT;

    /**
     * 多数据源配置
     */
    private Map<String, EsNode> nodes=new HashMap<>();

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public Map<String, EsNode> getNodes() {
        return nodes;
    }

    public void setNodes(Map<String, EsNode> nodes) {
        this.nodes = nodes;
    }

    /**
     * 单个 ES 节点配置
     */
    public static class EsNode {
        /**
         * ES 节点地址
         */
        private String host = "http://localhost:9200";

        /**
         * 账号
         */
        private String username;
        /**
         * 密码
         */
        private String password;
        /**
         * 连接超时时间 ms
         */
        private int connectTimeout = 5000;
        /**
         * 读取超时 ms
         */
        private int socketTimeout = 30000;
        /**
         * 获取连接超时 ms
         */
        private int connectionRequestTimeout = 5000;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }


        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public int getSocketTimeout() {
            return socketTimeout;
        }

        public void setSocketTimeout(int socketTimeout) {
            this.socketTimeout = socketTimeout;
        }

        public int getConnectionRequestTimeout() {
            return connectionRequestTimeout;
        }

        public void setConnectionRequestTimeout(int connectionRequestTimeout) {
            this.connectionRequestTimeout = connectionRequestTimeout;
        }
    }
}
