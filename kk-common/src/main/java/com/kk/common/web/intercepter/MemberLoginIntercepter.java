package com.kk.common.web.intercepter;


import com.kk.common.constant.SystemSource;
import com.kk.common.exception.BusinessException;
import com.kk.common.model.LoginDto;
import com.kk.common.model.MemberLoginDto;
import com.kk.common.utils.JsonUtil;
import com.kk.common.utils.MemberRedisUtil;
import com.kk.common.utils.RedisUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Enumeration;

/**
 * @Author: kk
 * @Date: 2025/11/18 17:24
 * 会员登录拦截器
 */
public class MemberLoginIntercepter implements HandlerInterceptor {

    private Logger log = LogManager.getRootLogger();
    @Value("${login.jwt.secretKey:2b65e17e6d86e95b6fdd0d489dd85ee6f834fded12773c5b33f8b88d685b28d1}")
    public String LoginJwtSecretKey;
    @Resource
    public MemberRedisUtil memberRedisUtil;
    /**
     * 进入controller方法之前
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        log.info("{}|{}", token, LoginJwtSecretKey);
        if (StringUtils.isEmpty(token)) {
            throw new BusinessException("token 不能为空！");
        }

        String loginDtoStr = (String) memberRedisUtil.get(token);
        if (StringUtils.isEmpty(loginDtoStr)) {
            throw new BusinessException("登录信息已过期，请重新登录！");
        }
        MemberLoginDto loginDto = (MemberLoginDto) JsonUtil.parseObject(loginDtoStr, MemberLoginDto.class);
        if (loginDto == null || new Date().after(loginDto.getExpireTime()))
        {
            throw new BusinessException("登录信息已过期，请重新登录！");
        }
        return !request.getMethod().equals("OPTIONS");

        //return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    public String getHeader(HttpServletRequest request) {
        StringBuilder str = new StringBuilder();
        Enumeration e = request.getHeaderNames();

        while(e.hasMoreElements()) {
            String key = (String)e.nextElement();
            str.append(key).append(":").append(request.getHeader(key)).append("||");
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
