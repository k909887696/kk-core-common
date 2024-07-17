package com.kk.common.dao.sqlinjector.sqlmethod;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.List;
import java.util.function.Predicate;


/**
 * @Author: kk
 * @Date: 2021/12/11 9:50
 * 批量插入方法实现
 */

@Slf4j
public class InsertDuplicateKeyUpdate extends AbstractMethod {

    private Predicate<TableFieldInfo> predicate;

    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
        SqlMethod sqlMethod = SqlMethod.INSERT_ONE;
        String sqlTemplate = "<script>\nINSERT  INTO %s %s VALUES %s\n ON DUPLICATE KEY update %s </script>";
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        String insertSqlColumn = tableInfo.getKeyInsertSqlColumn(true, false) + this.filterTableFieldInfo(fieldList, this.predicate, TableFieldInfo::getInsertSqlColumn, "");
        String columnScript = "(" + insertSqlColumn.substring(0, insertSqlColumn.length() - 1) + ")";
        String insertSqlProperty = tableInfo.getKeyInsertSqlProperty(true, "et.", false) + this.filterTableFieldInfo(fieldList, this.predicate, (i) -> {
            return i.getInsertSqlProperty("et.");
        }, "");
        insertSqlProperty = "(" + insertSqlProperty.substring(0, insertSqlProperty.length() - 1) + ")";
        String valuesScript = SqlScriptUtils.convertForeach(insertSqlProperty, "list", (String)null, "et", ",");

        String updateSqlProperty = this.filterTableFieldInfo(fieldList, this.predicate, (i) -> {
            return i.getColumn()+"= values("+i.getColumn()+")";
        }, ",");
        String updateScript = updateSqlProperty;
        String keyProperty = null;
        String keyColumn = null;
        if (tableInfo.havePK()) {
            if (tableInfo.getIdType() == IdType.AUTO) {
                keyGenerator = Jdbc3KeyGenerator.INSTANCE;
                keyProperty = tableInfo.getKeyProperty();
                keyColumn = tableInfo.getKeyColumn();
            } else if (null != tableInfo.getKeySequence()) {
                keyGenerator = TableInfoHelper.genKeyGenerator(this.getMethod(sqlMethod), tableInfo, this.builderAssistant);
                keyProperty = tableInfo.getKeyProperty();
                keyColumn = tableInfo.getKeyColumn();
            }
        }

        String sql = String.format(sqlTemplate, tableInfo.getTableName(), columnScript, valuesScript,updateScript);
        SqlSource sqlSource = this.languageDriver.createSqlSource(this.configuration, sql, modelClass);
        return this.addInsertMappedStatement(mapperClass, modelClass, this.getMethod(sqlMethod), sqlSource, (KeyGenerator)keyGenerator, keyProperty, keyColumn);
    }

    public String getMethod(SqlMethod sqlMethod) {
        return "insertDuplicateKeyUpdate";
    }

    public InsertDuplicateKeyUpdate() {
    }

    public InsertDuplicateKeyUpdate(final Predicate<TableFieldInfo> predicate) {
        this.predicate = predicate;
    }

    public InsertDuplicateKeyUpdate setPredicate(final Predicate<TableFieldInfo> predicate) {
        this.predicate = predicate;
        return this;
    }
}