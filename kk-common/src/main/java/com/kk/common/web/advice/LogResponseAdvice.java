package com.kk.common.web.advice;

import com.kk.common.constant.SystemKey;
import com.kk.common.trace.TraceData;
import com.kk.common.utils.CommonUtil;
import com.kk.common.utils.DateUtil;
import com.kk.common.utils.JsonUtil;
import com.kk.common.utils.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Date;

/**
 * @Author: kk
 * @Date: 2021/11/19 16:56
 */

@RestControllerAdvice(
        basePackages = {"com.kk.api","com.kk.common"}
)
public class LogResponseAdvice implements ResponseBodyAdvice<Object> {
    private Logger logger = LogManager.getLogger(LogResponseAdvice.class);

    public LogResponseAdvice() {
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }


    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        String url = request.getURI().toString();
        String bodyJson = JsonUtil.getJSONString(body);

        String startTimeStr = response.getHeaders().getFirst(SystemKey.REQUEST_START_TIME);
        long start = StringUtils.isNotBlank(startTimeStr)? Long.parseLong( startTimeStr):System.currentTimeMillis();
        long elapsed = DateUtil.elapsedTimeMillis( start);

        String logKey = CommonUtil.getRequestUrlMethod(url);
        LogUtil.info(logger,logKey," 响应ip: {} | url = {} | elapsed = {} ms | body = {}", new Object[]{  request.getRemoteAddress().getHostString(), url, elapsed, bodyJson});
        return body;
    }

}

