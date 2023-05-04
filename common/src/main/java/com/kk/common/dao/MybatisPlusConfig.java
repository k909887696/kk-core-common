package com.kk.common.dao;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

import com.kk.common.dao.sqlinjector.CustomBatchSqlInjector;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @Author: kk
 * @Date: 2021/12/7 14:21
 */
@Configuration

public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return mybatisPlusInterceptor;
    }

    @Value("${mybatis-plus-config.tx-method-timeout:600}")
    private int TX_METHOD_TIMEOUT ;
    @Value("${mybatis-plus-config.tx-method-pointcut:execution(* com.kk.business..service..*.*(..))}")
    private String TX_METHOD_POINTCUT;
    @Resource
    public TransactionManager transactionManager;
    // 事务的实现Advice
    /**
     * 批量处理sql注入器 注册spring ioc
     * @return 批量处理sql注入器
     */
    @Bean
    @Primary
    public CustomBatchSqlInjector customBatchSqlInjector() {
        return new CustomBatchSqlInjector();
    }
    @Bean
    public TransactionInterceptor txAdvice( ) {
        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
        RuleBasedTransactionAttribute readOnlyTx = new RuleBasedTransactionAttribute();
        readOnlyTx.setReadOnly(true);
        readOnlyTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);

        RuleBasedTransactionAttribute readOnlyTxNotSupported = new RuleBasedTransactionAttribute();
        readOnlyTxNotSupported.setReadOnly(true);
        readOnlyTxNotSupported.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);

        RuleBasedTransactionAttribute requiredTx = new RuleBasedTransactionAttribute();
        requiredTx.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        requiredTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        requiredTx.setTimeout(TX_METHOD_TIMEOUT);

        RuleBasedTransactionAttribute requiredRequireNew = new RuleBasedTransactionAttribute();
        requiredRequireNew.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        requiredRequireNew.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        requiredRequireNew.setTimeout(TX_METHOD_TIMEOUT);

        RuleBasedTransactionAttribute requiredNested = new RuleBasedTransactionAttribute();
        requiredNested.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        requiredNested.setPropagationBehavior(TransactionDefinition.PROPAGATION_NESTED);
        requiredNested.setTimeout(TX_METHOD_TIMEOUT);

        Map<String, TransactionAttribute> txMap = new HashMap<>();
        txMap.put("nots*", readOnlyTxNotSupported);
        txMap.put("search*", readOnlyTx);
        txMap.put("load*", readOnlyTx);
        txMap.put("download*", readOnlyTx);
        txMap.put("list*", readOnlyTx);
        txMap.put("valid*", readOnlyTx);
        txMap.put("find*", readOnlyTx);
        txMap.put("get*", readOnlyTx);
        txMap.put("query*", readOnlyTx);

        txMap.put("stand*", requiredRequireNew);

        txMap.put("netsed*", requiredNested);

        txMap.put("*", requiredTx);
        source.setNameMap(txMap);
        TransactionInterceptor txAdvice = new TransactionInterceptor(transactionManager, source);
        return txAdvice;
    }

    // 切面的定义,pointcut及advice
    @Bean
    public Advisor txAdviceAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(TX_METHOD_POINTCUT);
        return new DefaultPointcutAdvisor(pointcut, txAdvice( ));
    }


}
