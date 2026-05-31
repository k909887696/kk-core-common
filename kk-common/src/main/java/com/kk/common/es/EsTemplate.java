package com.kk.common.es;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Elasticsearch 模板工具类
 * RestHighLevelClient 封装（兼容 ES 7.10.1）
 * 同步操作，无队列无线程池
 *
 * @Author: kk
 */
public class EsTemplate {

    private static final Logger LOGGER = LogManager.getLogger(EsTemplate.class);

    private final RestHighLevelClient client;
    private final String defaultIndex;
    private final ObjectMapper objectMapper;

    private EsTemplate(Builder builder) {
        this.client = builder.client;
        this.defaultIndex = builder.defaultIndex;
        this.objectMapper = new ObjectMapper();
    }

    public static class Builder {
        private RestHighLevelClient client;
        private String defaultIndex;

        public Builder(RestHighLevelClient client) {
            this.client = client;
        }

        public Builder defaultIndex(String defaultIndex) {
            this.defaultIndex = defaultIndex;
            return this;
        }

        public EsTemplate build() {
            if (client == null) {
                throw new IllegalArgumentException("RestHighLevelClient is required");
            }
            return new EsTemplate(this);
        }
    }

    private String index(String index) {
        return (index != null && !index.isEmpty()) ? index : defaultIndex;
    }

    private void log(String action, String idx, Throwable e) {
        LOGGER.error("ES {} failed [{}]", action, idx, e);
    }

    // ==================== 索引操作 ====================

    public boolean existsIndex(String index) {
        String idx = index(index);
        try {
            return client.indices().exists(new GetIndexRequest(idx), RequestOptions.DEFAULT);
        } catch (IOException e) {
            log("existsIndex", idx, e);
            return false;
        }
    }

    public boolean existsIndex() {
        return existsIndex(null);
    }

    public boolean createIndex(String index, String settingsJson, String mappingJson) {
        String idx = index(index);
        try {
            CreateIndexRequest request = new CreateIndexRequest(idx);
            if (settingsJson != null && !settingsJson.isEmpty()) {
                request.settings(settingsJson, XContentType.JSON);
            }
            if (mappingJson != null && !mappingJson.isEmpty()) {
                request.mapping(mappingJson, XContentType.JSON);
            }
            AcknowledgedResponse response = client.indices().create(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (IOException e) {
            log("createIndex", idx, e);
            return false;
        }
    }

    public boolean createIndex(String index) {
        return createIndex(index, null, null);
    }

    public boolean createIndex() {
        return createIndex(null, null, null);
    }

    public boolean deleteIndex(String index) {
        String idx = index(index);
        try {
            org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest request =
                    new org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest(idx);
            AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (IOException e) {
            log("deleteIndex", idx, e);
            return false;
        }
    }

    // ==================== 文档操作 ====================

    public String saveOrUpdate(String index, String id, String json) {
        String idx = index(index);
        try {
            IndexRequest request = new IndexRequest(idx);
            if (id != null && !id.isEmpty()) {
                request.id(id);
            }
            request.source(json, XContentType.JSON);
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            return response.getId();
        } catch (IOException e) {
            log("saveOrUpdate", idx, e);
            return null;
        }
    }

    public String saveOrUpdate(String index, String id, Map<String, Object> doc) {
        String idx = index(index);
        try {
            IndexRequest request = new IndexRequest(idx);
            if (id != null && !id.isEmpty()) {
                request.id(id);
            }
            request.source(doc);
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            return response.getId();
        } catch (IOException e) {
            log("saveOrUpdate", idx, e);
            return null;
        }
    }

    public boolean update(String index, String id, String json) {
        String idx = index(index);
        try {
            UpdateRequest request = new UpdateRequest(idx, id);
            request.doc(json, XContentType.JSON);
            client.update(request, RequestOptions.DEFAULT);
            return true;
        } catch (IOException e) {
            log("update", idx, e);
            return false;
        }
    }

    public boolean update(String index, String id, Map<String, Object> doc) {
        String idx = index(index);
        try {
            UpdateRequest request = new UpdateRequest(idx, id).doc(doc);
            client.update(request, RequestOptions.DEFAULT);
            return true;
        } catch (IOException e) {
            log("update", idx, e);
            return false;
        }
    }


    public boolean delete(String index, String id) {
        String idx = index(index);
        try {
            DeleteRequest request = new DeleteRequest(idx, id);
            client.delete(request, RequestOptions.DEFAULT);
            return true;
        } catch (IOException e) {
            log("delete", idx, e);
            return false;
        }
    }

    public String get(String index, String id) {
        String idx = index(index);
        try {
            GetRequest request = new GetRequest(idx, id);
            GetResponse response = client.get(request, RequestOptions.DEFAULT);
            if (response.isExists()) {
                return objectMapper.writeValueAsString(response.getSource());
            }
            return null;
        } catch (IOException e) {
            log("get", idx, e);
            return null;
        }
    }

    public Map<String, Object> getMap(String index, String id) {
        String idx = index(index);
        try {
            GetRequest request = new GetRequest(idx, id);
            GetResponse response = client.get(request, RequestOptions.DEFAULT);
            if (response.isExists()) {
                return response.getSource();
            }
            return null;
        } catch (IOException e) {
            log("getMap", idx, e);
            return null;
        }
    }

    // ==================== 批量操作 ====================

    /**
     * 批量插入或更新文档
     * ES 的 IndexRequest 在 Bulk 中如果 ID 已存在则执行更新，不存在则插入
     */
    public BulkResponse bulkSaveOrUpdateMap(String indexName, List<Map<String, Object>> dataList) throws IOException {

        BulkRequest bulkRequest = new BulkRequest();

        for (Map<String, Object> data : dataList) {
            // 假设 Map 中包含 "id" 字段作为文档ID，如果没有则ES会自动生成
            Object id = data.get("id");
            IndexRequest request = new IndexRequest(indexName);
            if (id != null) {
                request.id(id.toString());
            }
            request.source(data, XContentType.JSON);
            bulkRequest.add(request);
        }
        BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        // 发送批量请求
        return response;
    }

    /**
     * 批量插入或更新文档（传入 JSON 字符串列表）
     * @param jsonList 包含完整文档内容的 JSON 字符串列表
     *                 如果需要在 JSON 内部指定 _id，请在 JSON 中包含 "id" 字段
     */
    public BulkResponse bulkSaveOrUpdateJson(String indexName, List<String> jsonList) throws IOException {

        BulkRequest bulkRequest = new BulkRequest();

        for (String jsonStr : jsonList) {
            // 假设你的 JSON 字符串里包含 "id" 字段用来作为 ES 的文档ID
            // 如果你的业务逻辑是自动生成 ID，可以跳过解析提取 ID 的步骤
            String docId = extractIdFromJson(jsonStr);

            IndexRequest request = new IndexRequest(indexName);
            if (docId != null && !docId.isEmpty()) {
                request.id(docId);
            }
            // 直接将 JSON 字符串塞入 source，并指定类型为 JSON
            request.source(jsonStr, XContentType.JSON);
            bulkRequest.add(request);
        }
        BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        return response;
    }

    /**
     * 简单的从 JSON 中提取 ID 的工具方法
     * （实际项目中建议使用 fastjson / jackson 等工具类来精准提取）
     */
    private String extractIdFromJson(String jsonStr) {
        try {
            // 这里以 fastjson 为例，如果你用的是 jackson 或其他库请自行替换
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSON.parseObject(jsonStr);
            Object idObj = jsonObject.get("id");
            return idObj == null ? null : idObj.toString();
        } catch (Exception e) {
            // 如果解析失败，返回 null，让 ES 自动生成 ID
            return null;
        }
    }





    // ==================== 搜索操作 ====================

    public List<Map<String, Object>> search(String index, QueryBuilder query, int from, int size, String sortField) {
        String idx = index(index);
        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.from(from).size(size);
            if (query != null) {
                sourceBuilder.query(query);
            }
            if (sortField != null && !sortField.isEmpty()) {
                sourceBuilder.sort(sortField, SortOrder.ASC);
            }

            SearchRequest request = new SearchRequest(idx).source(sourceBuilder);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            List<Map<String, Object>> list = new ArrayList<>();
            for (org.elasticsearch.search.SearchHit hit : response.getHits().getHits()) {
                list.add(hit.getSourceAsMap());
            }
            return list;
        } catch (IOException e) {
            log("search", idx, e);
            return Collections.emptyList();
        }
    }

    public long count(String index, QueryBuilder query) {
        String idx = index(index);
        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            if (query != null) {
                sourceBuilder.query(query);
            }
            sourceBuilder.size(0);

            SearchRequest request = new SearchRequest(idx).source(sourceBuilder);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            return response.getHits().getTotalHits().value;
        } catch (IOException e) {
            log("count", idx, e);
            return 0;
        }
    }

    // ==================== 静态工厂 ====================

    public static EsTemplate of(RestHighLevelClient client, String index) {
        return new Builder(client).defaultIndex(index).build();
    }
}
