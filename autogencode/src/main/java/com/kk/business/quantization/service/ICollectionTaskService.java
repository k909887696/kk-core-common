package com.kk.business.quantization.service;

import com.kk.business.quantization.dao.entity.CollectionTask;
import com.github.jeffreyning.mybatisplus.service.IMppService;

import java.util.List;
import com.kk.common.base.model.PageResult;
import com.kk.common.base.model.BasePage;
/**
 * <p>
 *  服务类
 * </p>
 *
 * @author kk
 * @since 2022-10-17
 */
public interface ICollectionTaskService extends IMppService<CollectionTask> {

    /**
    * 分批批量插入
    * @param list 数据列表
    * @return
    */
    void insertIgnoreBatch(List<CollectionTask> list);
    /**
    * 分页获取结果集
    * @param vo 请求参数
    * @return 结果集
    */
    PageResult<CollectionTask> getPageResult(BasePage vo);

}
