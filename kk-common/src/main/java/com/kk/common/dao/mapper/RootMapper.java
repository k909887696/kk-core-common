package com.kk.common.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: kk
 * @Date: 2021/12/11 10:01
 * 自定义根mapper，需要拓展功能课继承次mapper
 *  --此类继承 mybatisplus-plus MppBaseMapper--
 */
public interface RootMapper<T> extends MppBaseMapper<T> {

    /**
     * 自定义批量插入 ignore 版本，会根据mysql主键冲突自动忽略插入数据
     * 如果要自动填充，@Param(xx) xx参数名必须是 list/collection/array 3个的其中之一
     */
    int insertIgnoreBatchSomeColumn(@Param("list") List<T> list);

    /**
     * 自定义批量插入 DuplicateKeyUpdate 版本，会根据mysql主键冲突 会更新
     * 如果要自动填充，@Param(xx) xx参数名必须是 list/collection/array 3个的其中之一
     */
    int insertDuplicateKeyUpdate(@Param("list") List<T> list);

    /**
     * 自定义批量更新，条件为主键
     * 如果要自动填充，@Param(xx) xx参数名必须是 list/collection/array 3个的其中之一
     */
    int updateBatch(@Param("list") List<T> list);

    /**
     * 复合主键批量更新
     * @param list
     * @return
     */
    int updateByMultiIdBatch(@Param("list") List<T> list);

    /**
     * 批量插入 mybatis-plus自带
     * @param list
     * @return
     */
    int insertBatchSomeColumn(@Param("list") List<T> list);
}
