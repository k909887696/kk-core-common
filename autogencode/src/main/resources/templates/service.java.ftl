package ${package.Service};

import ${package.Entity}.${entity};
import ${superServiceClassPackage};

import java.util.List;
import com.kk.common.base.model.PageResult;
import com.kk.common.base.model.BasePage;
/**
 * <p>
 * ${table.comment!} 服务类
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
<#if kotlin>
interface ${table.serviceName} : ${superServiceClass}<${entity}>
<#else>
public interface ${table.serviceName} extends ${superServiceClass}<${entity}> {

    /**
    * 分批批量插入
    * @param list 数据列表
    * @return
    */
    void insertIgnoreBatch(List<${entity}> list);
    /**
    * 分页获取结果集
    * @param vo 请求参数
    * @return 结果集
    */
    PageResult<${entity}> get${entity}PageResult(BasePage vo);

}
</#if>
