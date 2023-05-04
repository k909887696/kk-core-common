package com.kk.common.web.handler;

import com.kk.common.exception.BusinessException;
import com.kk.common.constant.ResponseCode;
import com.kk.common.trace.TraceData;
import com.kk.common.web.model.ApiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * @Author: kk
 * @Date: 2021/11/18 14:41
 * 异常统一处理器
 */

@RestControllerAdvice
public class MyExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(MyExceptionHandler.class);

    /**
     * 系统异常拦截
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ApiResult globalException(HttpServletRequest request, Exception ex)
    {
        logger.error("{}|{}","globalException;Message;"+ TraceData.traceId.get(),ex.getMessage());
        logger.error("{}|{}","globalException;StackTrace;"+ TraceData.traceId.get(),ex.getStackTrace());
        ApiResult result;
        if (ex instanceof BusinessException) {
            BusinessException b = (BusinessException)ex;
            result = new ApiResult(b.getCode(), b.getMessage());
        } else {
            result = new ApiResult(ResponseCode.SYSTEM_EXCEPTION.getCode(),
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
    public ApiResult validationMethodArgumentNotValidException(MethodArgumentNotValidException exception){

        BindingResult result = exception.getBindingResult();
        StringBuilder message = new StringBuilder();
          if (result.hasErrors()) {
              List<ObjectError> errors = result.getAllErrors();
              errors.forEach(p ->{
                  FieldError fieldError = (FieldError) p;

                  message.append(fieldError.getDefaultMessage());
                  logger.error("{}|{}","validationMethodArgumentNotValidException;Data check failure","object{"+fieldError.getObjectName()+"},field{"+fieldError.getField()+
                          "},errorMessage{"+fieldError.getDefaultMessage()+"}");
              });
          }
        logger.error("{}|{}","validationMethodArgumentNotValidException;Message",exception.getMessage());
        return new ApiResult(ResponseCode.BUSINESS_PARAMETER_EXCEPTION.getCode(),message.toString());
    }

    /**
     * 参数类型转换错误
     *
     * @param exception 错误
     * @return 错误信息
     */
    @ResponseBody
    @ExceptionHandler(HttpMessageConversionException.class)
    public ApiResult parameterTypeConversionException(HttpMessageConversionException exception){
        logger.error("{}|{}","parameterTypeConversionException",exception.getCause().getLocalizedMessage());
        return  new ApiResult(ResponseCode.INVALID_PARAMETER.getCode(),ResponseCode.INVALID_PARAMETER.getDesc()+":"+exception.getCause().getLocalizedMessage());
    }

}
