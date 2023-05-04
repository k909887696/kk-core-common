package ${package.Controller};


import org.springframework.web.bind.annotation.*;
<#if swagger>
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
</#if>
<#if restControllerStyle>
import org.springframework.web.bind.annotation.RestController;
<#else>
import org.springframework.stereotype.Controller;
</#if>
<#if superControllerClassPackage??>
import ${superControllerClassPackage};
</#if>

import javax.annotation.Resource;
import javax.validation.Valid;
import ${package.Service}.${table.serviceName};
import com.kk.common.web.model.ApiResult;
import com.kk.common.base.model.PageResult;
import com.kk.common.base.model.BasePage;
import ${package.Entity}.${entity};
/**
 * <p>
 * ${table.comment!} 前端控制器
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
<#if swagger>
 @Api(value = "<#if package.ModuleName?? && package.ModuleName != "">/${package.ModuleName}</#if>/api/v1/${table.name}",tags = "${table.comment!}")
</#if>
@ResponseBody
<#if restControllerStyle>
@RestController
<#else>
@Controller
</#if>
@RequestMapping("<#if package.ModuleName?? && package.ModuleName != "">/${package.ModuleName}</#if>/api/v1/${table.name}")
<#if kotlin>
class ${table.controllerName}<#if superControllerClass??> : ${superControllerClass}()</#if>
<#else>
<#if superControllerClass??>
public class ${table.controllerName} extends ${superControllerClass} {
<#else>
public class ${table.controllerName} {
</#if>

    @Resource
    public ${table.serviceName} service;

    @ApiOperation("获取分页结果集")
    @ApiImplicitParams(  {
    @ApiImplicitParam(name = "token", value = "身份令牌", paramType = "header", required = false, dataType = "String"),
    @ApiImplicitParam(name = "signature", value = "签名", paramType = "header", required = false, dataType = "String"),
    @ApiImplicitParam(name = "timestamp", value = "时间戳", paramType = "header", required = false, dataType = "String"),
    @ApiImplicitParam(name = "source", value = "来源（app/web/minotor）", paramType = "header", required = false, dataType = "String"),
    @ApiImplicitParam(name = "version", value = "版本号（1.0.0）", paramType = "header",  dataType = "String")
    })
    @PostMapping("/get_${table.name}_page_result")
    public ApiResult< PageResult<${entity}> > get${entity}PageResult(@Valid @RequestBody BasePage vo)   {

        return new  ApiResult(service.get${entity}PageResult(vo));

    }

}
</#if>
