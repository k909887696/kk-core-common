package com.kk.common.base.model;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;

/**
 * @Author: kk
 * @Date: 2021/12/17 16:01
 * 基础页类
 */
public class BasePage {
    /**
     * 页索引 默认1
     */
    @ApiModelProperty(value="页索引",example = "1")
    @Min(value = 1,message="当前页索引不能为空!")
    private int pageIndex;
    /**
     * 页大小 默认10
     */
    @ApiModelProperty(value="页大小",example = "10")
    @Min(value = 1,message="每页大小不能为空!")
    private int pageSize;

    public int getPageIndex() {
        if(pageIndex<=0)
            pageIndex=1;
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        if(pageSize<=0)
            pageSize=10;
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
