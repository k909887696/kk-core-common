package com.kk.common.web.intercepter;


import com.kk.common.constant.SystemKey;
import com.kk.common.trace.TraceData;
import com.kk.common.utils.CommonUtil;
import com.kk.common.utils.DateUtil;
import com.kk.common.utils.JsonUtil;
import com.kk.common.utils.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Locale;
import java.util.UUID;

/**
 * @Author: kk
 * @Date: 2021/11/18 17:24
 * 参数拦截器
 */
public class ParameterInterceptor implements HandlerInterceptor {
    private String[] excludedPageArray;
    private Logger logger = LogManager.getLogger();
    @Value("${test:true}")
    public boolean test;
    /**
     * 进入controller方法之前
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {


        String url = request.getRequestURL().toString();
        String json = "忽略(图片上传请求)";
        String body = "忽略(图片上传请求)";
        boolean ifUploadReq = false;
        String remoteAddr;

        if (this.excludedPageArray != null) {
            remoteAddr = request.getServletPath();
            String[] var10 = this.excludedPageArray;
            int var11 = var10.length;

            for(int var12 = 0; var12 < var11; ++var12) {
                String excludedPage = var10[var12];
                if (remoteAddr.indexOf(excludedPage) > 0) {
                    ifUploadReq = true;
                }
            }
        }

        if (!ifUploadReq) {
            json = JsonUtil.getJSONString(request.getParameterMap());
            body = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8")).replace("\n", "");
        }

        remoteAddr = request.getRemoteAddr();
        if (request.getHeader("X-Real-IP") != null) {
            remoteAddr = request.getHeader("X-Real-IP");
        }
        long bodySize = CommonUtil.getStringSizeInKB(body);
        String logKey = CommonUtil.getRequestUrlMethod(url)+";"+CommonUtil.getStringSizeLevel(bodySize);

        LogUtil.info(logger,logKey," 请求ip: {} | url = {} | params = {} | body = {} | bodyByteSize = {} KB | header = {} "
                , new Object[]{ remoteAddr, url, json, body,bodySize, this.getHeader(request)});
        return !request.getMethod().equals("OPTIONS");
    }

    public String getHeader(HttpServletRequest request) {
        StringBuilder str = new StringBuilder();
        Enumeration e = request.getHeaderNames();

        while(e.hasMoreElements()) {
            String key = (String)e.nextElement();
            str.append(key).append("=").append(request.getHeader(key)).append("||");
        }

        return str.toString();
    }
    /**
     * 调用完controller之后，视图渲染之前
     */
    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

    }
    /**
     * 整个完成之后，通常用于资源清理
     */
    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

    }
}
