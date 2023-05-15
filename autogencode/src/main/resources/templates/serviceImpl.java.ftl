package ${package.ServiceImpl};

import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import ${package.Service}.${table.serviceName};
import ${superServiceImplClassPackage};
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kk.common.base.model.PageResult;
import ${package.Other}.vo.${entity}ListVo;
import ${package.Other}.dto.${entity}ListDto;
import ${package.Other}.vo.${entity}AddVo;
import ${package.Other}.vo.${entity}EditVo;
import ${package.Other}.dto.${entity}Dto;
/**
 * <p>
 * ${table.comment!} 服务实现类
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Service
<#if kotlin>
open class ${table.serviceImplName} : ${superServiceImplClass}<${table.mapperName}, ${entity}>(), ${table.serviceName} {

}
<#else>
public class ${table.serviceImplName} extends ${superServiceImplClass}<${table.mapperName}, ${entity}> implements ${table.serviceName} {

    @Resource
    public ${table.mapperName} ${table.mapperName?uncap_first};
    @Resource
    public MapperUtils mapperUtils;
    /**
    * 分批批量插入
    * @param list 数据列表
    * @return
    */
    public void insertIgnoreBatch(List<${entity}> list)
    {

        if(list ==null || list.size()<=0) return ;
        int totalCount = list.size();
        int size = 1000;
        int index = 1;
        int totalPage = totalCount / size;
        if( totalPage * size < totalCount ) totalPage += 1;

        for(;index<=totalPage;index++)
        {
            List<${entity}> tempList = list.stream().skip((index-1)*size).limit(size).collect(Collectors.toList());
            ${table.mapperName?uncap_first}.insertIgnoreBatchSomeColumn(tempList);
        }
    }
    /**
    * 单条插入
    * @param vo 请求参数
    * @return 结果集
    */
    public void insert(${entity}AddVo vo)
    {
        ${entity} model = mapperUtils.map(vo,${entity}.class);
        ${table.mapperName?uncap_first}.insert(model);
    }
    /**
    * 更新
    * @param vo 请求参数
    * @return 结果集
    */
    public int update(${entity}EditVo vo)
    {
        ${entity} model = mapperUtils.map(vo,${entity}.class);
        int r = ${table.mapperName?uncap_first}.updateById(model);
        if(r != null)
        {
            throw new BusinessException("${table.comment!}更新失败!");
        }
    }
    /**
    * 单条查询
    * @param vo 请求参数
    * @return 结果集
    */
    public ${entity}Dto selectById(String id)
    {
        ${entity} model = ${table.mapperName?uncap_first}.selectById(id);
        ${entity}Dto dto = mapperUtils.map(model,${entity}Dto.class);
        return dto;
    }
    /**
    * 删除
    * @param vo 请求参数
    * @return 结果集
    */
    public int deleteById(String id)
    {
        int r = ${table.mapperName?uncap_first}.deleteById(id);
        if(r != null)
        {
            throw new BusinessException("${table.comment!}删除失败!");
        }
    }
    /**
    * 分页获取结果集
    * @param vo 请求参数
    * @return 结果集
    */
    public PageResult<${entity}ListDto>  selectPageList(${entity}ListVo vo){

        IPage<${entity}ListDto> page = new Page<>(vo.getPageIndex(),vo.getPageSize());
        page = ${table.mapperName?uncap_first}.selectPageList(page,vo);
        PageResult<${entity}ListDto>  pageResult = new PageResult<>();

        pageResult.setResult(page.getRecords());
        pageResult.setTotalCount(page.getTotal());
        pageResult.setPageIndex(vo.getPageIndex());
        pageResult.setPageSize(vo.getPageSize());

        return pageResult;
    }



}
</#if>
