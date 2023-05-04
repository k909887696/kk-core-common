package com.kk.common.base.model;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class PageResult<T> {

    /**
     * 结果集
     */
    @ApiModelProperty(value="结果集")
    private List<T> result;

    /**
     * 总条数
     */
    @ApiModelProperty(value="总条数")
    private long totalCount;
    /**
     * 当前页索引
     */
    @ApiModelProperty(value="当前页索引")
    private int pageIndex;
    /**
     * 页大小
     */
    @ApiModelProperty(value="页大小")
    private int pageSize;

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
