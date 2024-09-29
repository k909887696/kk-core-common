package com.kk.common.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * @Author: kk
 * @Date: 2021/12/8 17:38
 */
@Slf4j
public class httpUtil {

    private final static int CONNECT_TIMEOUT = 60000; // in milliseconds
    private final static String DEFAULT_ENCODING = "UTF-8";

    private final static List<String> agents = Arrays.asList( "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36");


    public static <T> T httpRestRequest(Map<String, Object> params, String url, Class<T> t) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();

        HttpMethod method = HttpMethod.POST;
        //httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");


        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<Map<String, Object>>(params, httpHeaders);
        //执行HTTP请求
        try {
            //restTemplate.
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
     * 以post方式调用第三方接口
     * @param url
     * @param paramsJson
     * @return
     */
    public static String doPut(String url, String paramsJson, Map<String, Object> header) {

        CloseableHttpClient   httpClient = HttpClientBuilder.create().build();

        HttpPut httpPost = new HttpPut(url);
        Random rand = new Random();
        httpPost.addHeader(HttpHeaders.USER_AGENT, agents.get(rand.nextInt(agents.size())));

        if(header !=null && header.size()>0)
        {
            for(String k : header.keySet())
            {
                httpPost.addHeader(k,header.get(k).toString());
            }
        }
        try {
            StringEntity se = new StringEntity(paramsJson);
            //se.setContentEncoding("UTF-8");
            //发送json数据需要设置contentType
            se.setContentType("application/json;charset=UTF-8");
            //设置请求参数
            httpPost.setEntity(se);
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                //返回json格式
                String res = EntityUtils.toString(response.getEntity());
                return res;
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("{}|{}", "doPost;Message", e.getMessage());
            log.error("{}|{}", "doPost;StackTrace", e.getStackTrace());
        } finally {
            if (httpClient != null){
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String doPost(String url, String paramsJson, Map<String, Object> header) {

        CloseableHttpClient   httpClient = HttpClientBuilder.create().build();

        HttpPost httpPost = new HttpPost(url);
        Random rand = new Random();
        httpPost.addHeader(HttpHeaders.USER_AGENT, agents.get(rand.nextInt(agents.size())));

        if(header !=null && header.size()>0)
        {
            for(String k : header.keySet())
            {
                httpPost.addHeader(k,header.get(k).toString());
            }
        }
        try {
            StringEntity se = new StringEntity(paramsJson);
            //se.setContentEncoding("UTF-8");
            //发送json数据需要设置contentType
            se.setContentType("application/json;charset=UTF-8");
            //设置请求参数
            httpPost.setEntity(se);
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                //返回json格式
                String res = EntityUtils.toString(response.getEntity());
                return res;
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("{}|{}", "doPost;Message", e.getMessage());
            log.error("{}|{}", "doPost;StackTrace", e.getStackTrace());
        } finally {
            if (httpClient != null){
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
        //httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
        if(header !=null && header.size()>0)
        {
            for(String k : header.keySet())
            {
                httpHeaders.set(k,header.get(k).toString());
            }
        }

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<Map<String, Object>>(params, httpHeaders);
        //执行HTTP请求
        try {
            //restTemplate.
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

        // 构造URL

        URL url = new URL(urlString);

        // 打开连接

        URLConnection con = url.openConnection();

        // 输入流

        InputStream is = con.getInputStream();

        // 1K的数据缓冲

        byte[] bs = new byte[1024];

        // 读取到的数据长度

        int len;

        // 输出的文件流

        String filename = savePath;  //下载路径及下载图片名称

        File file = new File(filename);

        FileOutputStream os = new FileOutputStream(file, true);

        // 开始读取

        while ((len = is.read(bs)) != -1) {

            os.write(bs, 0, len);

        }

        System.out.println("保存成功："+savePath);

        // 完毕，关闭所有链接

        os.close();

        is.close();

    }
}
