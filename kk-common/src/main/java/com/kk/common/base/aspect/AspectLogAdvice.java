package com.kk.common.base.aspect;

import com.kk.common.trace.TraceData;
import com.kk.common.utils.DateUtil;
import com.kk.common.utils.JsonUtil;
import com.kk.common.utils.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AspectLogAdvice  implements MethodInterceptor {

    private static final org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger(AspectLogAdvice.class);

    @Value("${aspect-log.pointcut-expression:execution(* com.kk.business..service..*.*(..))}")
    public String aspectLogPointcutExpression;
    /**
     *  是否全部记录
     *  true：一直记录
     *  false：异常记录
     */
    @Value("${aspect-log.always:false}")
    public boolean aspectLogAlways;
    /**
     * 超时记录时间 ms 默认10000ms
     */
    @Value("${aspect-log.max-span-time:10000}")
    public int aspectLogMaxSpanTime;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        long start = System.currentTimeMillis();
        StringBuffer from = new StringBuffer();
        from.append(JsonUtil.getJSONString(invocation.getArguments()));
        String seqNo = TraceData.traceId.get();
        String methodName = invocation.getThis().getClass().getSimpleName()+";"+invocation.getMethod().getName();


        Map<String,String> logParams = new HashMap<>();
        logParams.put("LogType","Intercept");
        logParams.put("IsException","NO");
        Object proceed = null;
        try {
            proceed = invocation.proceed();
        } catch (Exception e) {
            logParams.put("SpanTime",(System.currentTimeMillis()-start)+ "");
            logParams.put("IsException","Yes");
            LogUtil.error(logger,methodName,"elapsed={} ms | input:{} ---- result:{} | {} ", DateUtil.elapsedTimeMillis(start), from.toString(), ExceptionUtils.getStackTrace(e),logParams);
            throw e;
        }finally {
            //默认正常的拦截日志，不记录，只有配置了1才记录
            if( aspectLogAlways){
                logParams.put("SpanTime",(System.currentTimeMillis()-start)+"");
                LogUtil.info(logger,methodName,"elapsed={} ms | input:{} ---- result:{} | {} ", DateUtil.elapsedTimeMillis(start),from.toString(),JsonUtil.getJSONString(proceed),logParams,methodName);
            }else if(aspectLogMaxSpanTime>0){
                long spantime = System.currentTimeMillis() - start;
                if(spantime>(aspectLogMaxSpanTime)){
                    logParams.put("SpanTime",(System.currentTimeMillis()-start)+"");
                    LogUtil.info(logger,methodName+";timeout","elapsed= {} ms | input:{} ---- result:{} | {} ", DateUtil.elapsedTimeMillis(start),from.toString(),JsonUtil.getJSONString(proceed),logParams);
                }
            }
        }

        return proceed;
    }
}
