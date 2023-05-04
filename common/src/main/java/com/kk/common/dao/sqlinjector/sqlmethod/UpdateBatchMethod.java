package com.kk.common.dao.sqlinjector.sqlmethod;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Author: kk
 * @Date: 2021/12/11 9:52
 * 批量更新方法实现，条件为主键，选择性更新
 */
@Slf4j
public class UpdateBatchMethod extends AbstractMethod {
    private String getCol(List<TableFieldInfo> fieldList, String attrName) {
        Iterator var3 = fieldList.iterator();

        TableFieldInfo tableFieldInfo;
        String prop;
        do {
            if (!var3.hasNext()) {
                throw new RuntimeException("not found column for " + attrName);
            }

            tableFieldInfo = (TableFieldInfo)var3.next();
            prop = tableFieldInfo.getProperty();
        } while(!prop.equals(attrName));

        return tableFieldInfo.getColumn();
    }

    private String createWhere(Class<?> modelClass, TableInfo tableInfo) {
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        Field[] fieldArray = modelClass.getDeclaredFields();
        Map<String, String> idMap = new HashMap();
        Field[] var6 = fieldArray;
        int var7 = fieldArray.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            Field field = var6[var8];
            MppMultiId mppMultiId = (MppMultiId)field.getAnnotation(MppMultiId.class);
            if (mppMultiId != null) {
                String attrName = field.getName();
                String colName = this.getCol(fieldList, attrName);
                idMap.put(attrName, colName);
            }
        }

        if (idMap.isEmpty()) {
            log.info("entity {} not contain MppMultiId anno", modelClass.getName());
            return null;
        } else {
            StringBuilder sb = new StringBuilder("");
            idMap.forEach((attrNamex, colNamex) -> {
                if (sb.length() > 0) {
                    sb.append(" and ");
                }

                sb.append(colNamex).append("=").append("#{item.").append(attrNamex).append("}");
            });
            return sb.toString();
        }
    }
    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {

            String sql = "<script>\n<foreach collection=\"list\" item=\"item\" separator=\";\">\nupdate %s %s where %s=#{%s} %s\n</foreach>\n</script>";
            String additional = tableInfo.isWithVersion() ? tableInfo.getVersionFieldInfo().getVersionOli("item", "item.") : "" + tableInfo.getLogicDeleteSql(true, true);
            String setSql = sqlSet(tableInfo.isWithLogicDelete(), false, tableInfo, false, "item", "item.");
            String sqlResult = String.format(sql, tableInfo.getTableName(), setSql, tableInfo.getKeyColumn(), "item." + tableInfo.getKeyProperty(), additional);
            //log.debug("sqlResult----->{}", sqlResult);
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, sqlResult, modelClass);
            // 第三个参数必须和RootMapper的自定义方法名一致
            return this.addUpdateMappedStatement(mapperClass, modelClass, "updateBatch", sqlSource);



    }
}