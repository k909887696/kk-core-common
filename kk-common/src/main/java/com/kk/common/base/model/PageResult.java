package com.kk.common.base.model;



import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "数据分页结果类")
public class PageResult<T> {

    public PageResult<T> convertPage(Page<T> page) {
        if (page == null) {
            return null;
        } else {
            PageResult<T> rspVo = new PageResult<>();
            rspVo.setResult(page.getRecords());
            rspVo.setPageIndex((int)page.getCurrent());
            rspVo.setPageSize(page.getSize());
            rspVo.setTotalCount(page.getTotal());
            return rspVo;
        }
    }
    /**
     * 结果集
     */
    @Schema(description="结果集")
    private List<T> result;

    /**
     * 总条数
     */
    @Schema(description="总条数")
    private long totalCount;
    /**
     * 当前页索引
     */
    @Schema(description="当前页索引")
    private int pageIndex;
    /**
     * 页大小
     */
    @Schema(description="页大小")
    private long pageSize;

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

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }
}
