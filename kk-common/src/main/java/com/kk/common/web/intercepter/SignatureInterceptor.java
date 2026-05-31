package com.kk.common.web.intercepter;


import com.kk.common.utils.CommonUtil;
import com.kk.common.utils.JsonUtil;
import com.kk.common.utils.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.nio.charset.Charset;
import java.util.Enumeration;

/**
 * @Author: kk
 * @Date: 2021/11/18 17:24
 * 签名拦截器（用于外部接口无登录情况）
 */
public class SignatureInterceptor implements HandlerInterceptor {
    private String[] excludedPageArray;
    private Logger logger = LogManager.getLogger();

    /**
     * 进入controller方法之前
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {



        return !request.getMethod().equals("OPTIONS");
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
