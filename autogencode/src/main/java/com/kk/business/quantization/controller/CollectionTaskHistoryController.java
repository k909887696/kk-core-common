package com.kk.business.quantization.controller;


import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import com.kk.business.quantization.service.ICollectionTaskHistoryService;
import com.kk.common.web.model.ApiResult;
import com.kk.common.base.model.PageResult;
import com.kk.common.base.model.BasePage;
import com.kk.business.quantization.dao.entity.CollectionTaskHistory;
/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author kk
 * @since 2022-10-17
 */
 @Api(value = "/quantization/api/v1/collection_task_history",tags = "")
@ResponseBody
@RestController
@RequestMapping("/quantization/api/v1/collection_task_history")
public class CollectionTaskHistoryController {

    @Resource
    public ICollectionTaskHistoryService service;

    @ApiOperation("获取分页结果集")
    @ApiImplicitParams(  {
    @ApiImplicitParam(name = "token", value = "身份令牌", paramType = "header", required = false, dataType = "String"),
    @ApiImplicitParam(name = "signature", value = "签名", paramType = "header", required = false, dataType = "String"),
    @ApiImplicitParam(name = "timestamp", value = "时间戳", paramType = "header", required = false, dataType = "String"),
    @ApiImplicitParam(name = "source", value = "来源（app/web/minotor）", paramType = "header", required = false, dataType = "String"),
    @ApiImplicitParam(name = "version", value = "版本号（1.0.0）", paramType = "header",  dataType = "String")
    })
    @PostMapping("/get_collection_task_history_page_result")
    public ApiResult< PageResult<CollectionTaskHistory> > getCollectionTaskHistoryPageResult(@Valid @RequestBody BasePage vo)   {

        return new  ApiResult(service.getPageResult(vo));

    }

}
