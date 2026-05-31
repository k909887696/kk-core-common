package com.kk.common.web.filter;

import com.kk.common.constant.SystemKey;
import com.kk.common.trace.TraceData;
import com.kk.common.utils.CommonUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import jakarta.servlet.*;

import java.io.IOException;

/**
 * created with IntelliJ IDEA.
 * 基础过滤器
 * 设置请求响应编码
 * 记录请求时间
 * 1. 为每个请求生成唯一的TraceID
 * 2. 将TraceID设置到MDC中，供日志使用
 * 3. 将TraceID添加到响应头中，供前端追踪
 * auth: kk
 * date: 2026-05-12
 */

public class BaseFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        //设置编码格式
        request.setCharacterEncoding(SystemKey.ENCODING); // 设置请求编码格式
        response.setCharacterEncoding(SystemKey.ENCODING); // 设置响应编码格式
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            // 1. 从请求头获取TraceID（如果是微服务调用）
            String traceId = httpRequest.getHeader(SystemKey.TRACE_ID_KEY);

            // 2. 如果没有TraceID，生成新的
            if (traceId == null || traceId.isEmpty()) {
                traceId = CommonUtil.generateUUID();
            }

            // 3. 将TraceID放入MDC（Mapped Diagnostic Context）
            MDC.put(SystemKey.TRACE_ID_KEY, traceId);
            TraceData.traceId.set(traceId);

            // 4. 将TraceID添加到响应头
            httpResponse.setHeader(SystemKey.TRACE_ID_KEY, traceId);
            // 请求开始时间，单位毫秒
            Long startTime = System.currentTimeMillis();
            httpResponse.setHeader(SystemKey.REQUEST_START_TIME, startTime+"");
            TraceData.seqStart.set(startTime);
            // 5. 继续执行后续过滤器
            chain.doFilter(request, response);

        } finally {
            // 6. 清理MDC，避免内存泄漏
            MDC.remove(SystemKey.TRACE_ID_KEY);
        }
    }

}
