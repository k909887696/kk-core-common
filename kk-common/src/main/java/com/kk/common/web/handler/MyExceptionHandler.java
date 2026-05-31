package com.kk.common.web.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import com.kk.common.exception.BusinessException;
import com.kk.common.constant.ResponseCode;
import com.kk.common.trace.TraceData;
import com.kk.common.utils.CommonUtil;
import com.kk.common.utils.LogUtil;
import com.kk.common.web.model.ApiResult;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.List;


/**
 * @Author: kk
 * @Date: 2021/11/18 14:41
 * 异常统一处理器
 */

@RestControllerAdvice
public class MyExceptionHandler {
    private static final Logger logger = LogManager.getLogger(MyExceptionHandler.class);

    /**
     * 系统异常拦截
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ApiResult<?> globalException(HttpServletRequest request, Exception ex)
    {
        String methodName =  CommonUtil.getRequestUrlMethod(request.getRequestURL().toString());
        String logKey  = "globalException;"+methodName;

        LogUtil.error(logger,logKey,"{}|{}|{} ","globalException", ex.getMessage(),ex.getStackTrace());
        ApiResult<?> result;
        if (ex instanceof BusinessException) {
            BusinessException be = (BusinessException)ex;
            result = new ApiResult<>(be.getCode(), be.getMessage());
        } else  if (ex instanceof NotLoginException) {
            NotLoginException be = (NotLoginException )ex;
            result = new ApiResult<>(ResponseCode.LOGIN_OUT.getCode(), ResponseCode.LOGIN_OUT.getDesc());
        }else  if (ex instanceof NotPermissionException) {
            NotPermissionException be = (NotPermissionException )ex;
            result = new ApiResult<>(ResponseCode.NO_PERMISSION_OPERATION.getCode(), ResponseCode.NO_PERMISSION_OPERATION.getDesc());
        }
        else {
            result = new ApiResult<>(ResponseCode.SYSTEM_EXCEPTION.getCode(),
                    ResponseCode.SYSTEM_EXCEPTION.getDesc()+":"+ex.getMessage());
        }

        return result;

    }
    /**
     *  校验错误拦截处理
     *
     * @param exception 错误信息集合
     * @return 错误信息
     */

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<?> argNVException(HttpServletRequest request,MethodArgumentNotValidException exception){
        String methodName =  CommonUtil.getRequestUrlMethod(request.getRequestURL().toString());
        String logKey  = "argNVException;"+methodName;
        BindingResult result = exception.getBindingResult();
        StringBuilder message = new StringBuilder();
          if (result.hasErrors()) {
              List<ObjectError> errors = result.getAllErrors();
              errors.forEach(p ->{
                  FieldError fieldError = (FieldError) p;

                  message.append(fieldError.getDefaultMessage());
                  LogUtil.error(logger,logKey,"{}|{} ","argNVException:Data check failure","object{"+fieldError.getObjectName()+"},field{"+fieldError.getField()+
                          "},errorMessage{"+fieldError.getDefaultMessage()+"}");
              });
          }
        LogUtil.error(logger,logKey,"{}|{}|{} ","argNVException",exception.getMessage(),exception.getStackTrace());
        return new ApiResult<>(ResponseCode.BUSINESS_PARAMETER_EXCEPTION.getCode(),message.toString());
    }

    /**
     * 参数类型转换错误
     *
     * @param exception 错误
     * @return 错误信息
     */
    @ResponseBody
    @ExceptionHandler(HttpMessageConversionException.class)
    public ApiResult<?> msgConversionException(HttpServletRequest request,HttpMessageConversionException exception){
        String methodName =  CommonUtil.getRequestUrlMethod(request.getRequestURL().toString());
        String logKey  = "msgConversionException;"+methodName;
        LogUtil.error(logger,logKey,"{}|{}|{} ","msgConversionException",exception.getMessage(),exception.getStackTrace());
        return  new ApiResult<>(ResponseCode.INVALID_PARAMETER.getCode(),ResponseCode.INVALID_PARAMETER.getDesc()+":"+exception.getMessage());
    }

}
