package com.kk.business.quantization.service.impl;

import com.kk.business.quantization.dao.entity.CollectionTask;
import com.kk.business.quantization.dao.mapper.CollectionTaskMapper;
import com.kk.business.quantization.service.ICollectionTaskService;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kk.common.base.model.PageResult;
import com.kk.common.base.model.BasePage;
/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author kk
 * @since 2022-10-17
 */
@Service
public class CollectionTaskServiceImpl extends MppServiceImpl<CollectionTaskMapper, CollectionTask> implements ICollectionTaskService {

    @Resource
    public CollectionTaskMapper mapper;
    /**
    * 分批批量插入
    * @param list 数据列表
    * @return
    */
    public void insertIgnoreBatch(List<CollectionTask> list)
    {

        if(list ==null || list.size()<=0) return ;
        int totalCount = list.size();
        int size = 1000;
        int index = 1;
        int totalPage = totalCount / size;
        if( totalPage * size < totalCount ) totalPage += 1;

        for(;index<=totalPage;index++)
        {
            List<CollectionTask> tempList = list.stream().skip((index-1)*size).limit(size).collect(Collectors.toList());
            mapper.insertIgnoreBatchSomeColumn(tempList);
        }
    }
    /**
    * 分页获取结果集
    * @param vo 请求参数
    * @return 结果集
    */
    public PageResult<CollectionTask>  getPageResult(BasePage vo){

        LambdaQueryWrapper<CollectionTask> query = new LambdaQueryWrapper<>();
        IPage<CollectionTask> page = new Page<>(vo.getPageIndex(),vo.getPageSize());

        //这里开始编写查询条件

        page = mapper.selectPage(page,query);
        PageResult<CollectionTask>  pageResult = new PageResult<>();

        pageResult.setResult(page.getRecords());
        pageResult.setTotalCount(page.getTotal());
        pageResult.setPageIndex(vo.getPageIndex());
        pageResult.setPageSize(vo.getPageSize());

        return pageResult;
    }


}
