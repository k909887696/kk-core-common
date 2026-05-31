package com.kk.common.dao;

import com.kk.common.annotation.BaseTransactional;
import com.kk.common.exception.BusinessException;
import com.kk.common.utils.DateUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Date;

/**
 * created with IntelliJ IDEA.
 * description:
 * auth: kk
 * date: 2026-05-29
 */
@Aspect
@Component
public class BaseTransactionAspect {
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Value("${mybatis-plus-config.tx-method-timeout:60}")
    private int TX_METHOD_TIMEOUT ;
    public BaseTransactionAspect() {
    }

    @Around("@annotation(baseTransactional)")
    public Object around(ProceedingJoinPoint joinPoint, BaseTransactional baseTransactional) throws Throwable {
        int timeoutSeconds = baseTransactional.timeout();
        long startTime = System.currentTimeMillis();
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(baseTransactional.propagation().value());
        def.setTimeout(timeoutSeconds);
        def.setIsolationLevel(baseTransactional.isolation().value());
        def.setReadOnly(baseTransactional.readOnly());
        TransactionStatus status = this.transactionManager.getTransaction(def);

        try {
            Object result = joinPoint.proceed();
            if (DateUtil.elapsedTimeMillis(startTime) >= (long)(timeoutSeconds * 1000)) {
                throw new BusinessException("事务执行超时，已强制回滚。");
            } else {
                this.transactionManager.commit(status);
                return result;
            }
        } catch (Exception var9) {
            this.transactionManager.rollback(status);
            throw var9;
        }
    }
}
