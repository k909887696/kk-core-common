package com.kk.common.dao.sqlinjector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import com.github.jeffreyning.mybatisplus.base.MppSqlInjector;

import com.kk.common.dao.sqlinjector.sqlmethod.InsertDuplicateKeyUpdate;
import com.kk.common.dao.sqlinjector.sqlmethod.InsertIgnoreBatchSomeColumn;
import com.kk.common.dao.sqlinjector.sqlmethod.UpdateBatchMethod;
import com.kk.common.dao.sqlinjector.sqlmethod.UpdateByMultiIdBatchMethod;

import java.util.List;

/**
 * @Author: kk
 * @Date: 2021/12/11 9:53
 * 批量插入更新sql注入器
 */
public class CustomBatchSqlInjector extends MppSqlInjector {
    /**
     * 如果只需增加方法，保留mybatis plus自带方法，
     * 可以先获取super.getMethodList()，再添加add
     */
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass,tableInfo);
        methodList.add(new InsertIgnoreBatchSomeColumn());
        methodList.add(new UpdateBatchMethod());
        methodList.add(new UpdateByMultiIdBatchMethod());
        methodList.add(new InsertBatchSomeColumn());
        methodList.add(new InsertDuplicateKeyUpdate());

        return methodList;
    }
}
