package com.kk.common.utils;

import com.kk.common.exception.BusinessException;
import com.kk.common.model.LoginDto;
import com.kk.common.model.MemberLoginDto;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Member;
import java.util.Date;

/**
 * @Author：kk
 * @Date：2025/5/13 22:34
 */
public class LoginUtil {



    public static LoginDto getLoginInfo(String token)
    {

        RedisUtil redisUtil = SpringContextUtils.getBean(RedisUtil.class);
        String loginDtoStr = (String) redisUtil.get(token);
        if (StringUtils.isEmpty(loginDtoStr)) {
            throw new BusinessException("登录信息已过期，请重新登录！");
        }
        LoginDto loginDto = (LoginDto) JsonUtil.parseObject(loginDtoStr, LoginDto.class);
        if (loginDto == null || new Date().after(loginDto.getExpireTime()))
        {
            throw new BusinessException("登录信息已过期，请重新登录！");
        }
        return loginDto;
    }
    public static LoginDto getLoginInfo()
    {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String token = request.getHeader("token");
        RedisUtil redisUtil = SpringContextUtils.getBean(RedisUtil.class);
        String loginDtoStr = (String) redisUtil.get(token);
        if (StringUtils.isEmpty(loginDtoStr)) {
            throw new BusinessException("登录信息已过期，请重新登录！");
        }
        LoginDto loginDto = (LoginDto) JsonUtil.parseObject(loginDtoStr, LoginDto.class);
        if (loginDto == null || new Date().after(loginDto.getExpireTime()))
        {
            throw new BusinessException("登录信息已过期，请重新登录！");
        }
        return loginDto;
    }

    public static MemberLoginDto getMemberLoginInfo(String token)
    {
        MemberRedisUtil redisUtil = SpringContextUtils.getBean(MemberRedisUtil.class);
        String loginDtoStr = (String) redisUtil.get(token);
        if (StringUtils.isEmpty(loginDtoStr)) {
            throw new BusinessException("登录信息已过期，请重新登录！");
        }
        MemberLoginDto loginDto = (MemberLoginDto) JsonUtil.parseObject(loginDtoStr, MemberLoginDto.class);
        if (loginDto == null || new Date().after(loginDto.getExpireTime()))
        {
            throw new BusinessException("登录信息已过期，请重新登录！");
        }
        return loginDto;
    }
    public static MemberLoginDto getMemberLoginInfo()
    {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String token = request.getHeader("token");
        MemberRedisUtil redisUtil = SpringContextUtils.getBean(MemberRedisUtil.class);
        String loginDtoStr = (String) redisUtil.get(token);
        if (StringUtils.isEmpty(loginDtoStr)) {
            throw new BusinessException("登录信息已过期，请重新登录！");
        }
        MemberLoginDto loginDto = (MemberLoginDto) JsonUtil.parseObject(loginDtoStr, MemberLoginDto.class);
        if (loginDto == null || new Date().after(loginDto.getExpireTime()))
        {
            throw new BusinessException("登录信息已过期，请重新登录！");
        }
        return loginDto;
    }
}
