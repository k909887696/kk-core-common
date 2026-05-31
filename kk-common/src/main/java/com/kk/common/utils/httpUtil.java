package com.kk.common.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

/**
 * http请求工具类
 * @Author: kk
 * @Date: 2021/12/8 17:38
 */
@Slf4j
public class httpUtil {

    private final static int CONNECT_TIMEOUT = 60000; // in milliseconds
    private final static String DEFAULT_ENCODING = "UTF-8";

    private final static List<String> agents = Arrays.asList("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36");

    // 创建共享的 HttpClient 实例(线程安全)
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(CONNECT_TIMEOUT))
            .build();


    public static <T> T httpRestRequest(Map<String, Object> params, String url, Class<T> t) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();

        HttpMethod method = HttpMethod.POST;
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<Map<String, Object>>(params, httpHeaders);
        try {
            ResponseEntity<T> response = restTemplate.exchange(url,method, requestEntity, t);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("{}|{}", "httpRestRequest;Message", e.getMessage());
            log.error("{}|{}", "httpRestRequest;StackTrace", e.getStackTrace());
            return null;
        }
    }


    /**
     * 以put方式调用第三方接口
     * @param url
     * @param paramsJson
     * @param header
     * @return
     */
    public static String doPut(String url, String paramsJson, Map<String, Object> header) {
        Random rand = new Random();
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .PUT(HttpRequest.BodyPublishers.ofString(paramsJson, StandardCharsets.UTF_8))
                    .timeout(Duration.ofMillis(CONNECT_TIMEOUT))
                    .header(HttpHeaders.USER_AGENT, agents.get(rand.nextInt(agents.size())))
                    .header(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");

            if (header != null && header.size() > 0) {
                for (String k : header.keySet()) {
                    requestBuilder.header(k, header.get(k).toString());
                }
            }

            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpStatus.OK.value()) {
                return response.body();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            log.error("{}|{}", "doPut;Message", e.getMessage());
            log.error("{}|{}", "doPut;StackTrace", ExceptionUtils.getStackTrace(e));
            Thread.currentThread().interrupt();
        }
        return null;
    }

    /**
     * 以post方式调用第三方接口
     * @param url
     * @param paramsJson
     * @param header
     * @return
     */
    public static String doPost(String url, String paramsJson, Map<String, Object> header) {
        Random rand = new Random();
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(paramsJson, StandardCharsets.UTF_8))
                    .timeout(Duration.ofMillis(CONNECT_TIMEOUT))
                    .header(HttpHeaders.USER_AGENT, agents.get(rand.nextInt(agents.size())))
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .header(HttpHeaders.ACCEPT_ENCODING, "UTF-8");

            if (header != null && header.size() > 0) {
                for (String k : header.keySet()) {
                    requestBuilder.header(k, header.get(k).toString());
                }
            }

            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpStatus.OK.value()) {
                return response.body();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            log.error("{}|{}", "doPost;Message", e.getMessage());
            log.error("{}|{}", "doPost;StackTrace", ExceptionUtils.getStackTrace(e));
            Thread.currentThread().interrupt();
        }
        return null;
    }

    public static <T> T httpRestRequest(Map<String, Object> params, String url, Map<String, Object> header,Class<T> t){
        return httpRestRequest(params,url,header,t,HttpMethod.POST);
    }

    public static <T> T httpRestRequest(Map<String, Object> params, String url, Map<String, Object> header,Class<T> t,HttpMethod httpMethod) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();

        HttpMethod method = httpMethod;
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
        if(header !=null && header.size()>0) {
            for(String k : header.keySet()) {
                httpHeaders.set(k,header.get(k).toString());
            }
        }

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<Map<String, Object>>(params, httpHeaders);
        try {
            ResponseEntity<T> response = restTemplate.exchange(url,method, requestEntity, t);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("{}|{}", "httpRestRequest;Message", e.getMessage());
            log.error("{}|{}", "httpRestRequest;StackTrace", e.getStackTrace());
            return null;
        }
    }

    public static void download(String urlString, String savePath) throws Exception {
        URL url = new URL(urlString);
        URLConnection con = url.openConnection();
        InputStream is = con.getInputStream();
        byte[] bs = new byte[1024];
        int len;
        String filename = savePath;
        File file = new File(filename);
        FileOutputStream os = new FileOutputStream(file, true);

        while ((len = is.read(bs)) != -1) {
            os.write(bs, 0, len);
        }

        System.out.println("保存成功："+savePath);
        os.close();
        is.close();
    }
}
