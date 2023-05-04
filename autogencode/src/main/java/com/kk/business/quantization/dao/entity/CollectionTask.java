package com.kk.business.quantization.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author kk
 * @since 2022-10-17
 */
@TableName("collection_task")
@ApiModel(value = "对象", description = "")
public class CollectionTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("task_id")
    private String taskId;

    @TableField("name")
    private String name;

    @TableField("policy_id")
    private String policyId;

    @TableField("invoke_code")
    private String invokeCode;

    @TableField("invoke_method")
    private String invokeMethod;

    @TableField("invoke_params")
    private String invokeParams;

    @TableField("pre_run_time")
    private Date preRunTime;

    @TableField("run_time")
    private Date runTime;

    @TableField("finish_time")
    private Date finishTime;

    @TableField("create_id")
    private String createId;

    @TableField("create_name")
    private String createName;

    @TableField("create_time")
    private Date createTime;

    @TableField("run_count")
    private Integer runCount;

    @TableField("ex_msg")
    private String exMsg;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }
    public String getInvokeCode() {
        return invokeCode;
    }

    public void setInvokeCode(String invokeCode) {
        this.invokeCode = invokeCode;
    }
    public String getInvokeMethod() {
        return invokeMethod;
    }

    public void setInvokeMethod(String invokeMethod) {
        this.invokeMethod = invokeMethod;
    }
    public String getInvokeParams() {
        return invokeParams;
    }

    public void setInvokeParams(String invokeParams) {
        this.invokeParams = invokeParams;
    }
    public Date getPreRunTime() {
        return preRunTime;
    }

    public void setPreRunTime(Date preRunTime) {
        this.preRunTime = preRunTime;
    }
    public Date getRunTime() {
        return runTime;
    }

    public void setRunTime(Date runTime) {
        this.runTime = runTime;
    }
    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }
    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }
    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public Integer getRunCount() {
        return runCount;
    }

    public void setRunCount(Integer runCount) {
        this.runCount = runCount;
    }
    public String getExMsg() {
        return exMsg;
    }

    public void setExMsg(String exMsg) {
        this.exMsg = exMsg;
    }

    @Override
    public String toString() {
        return "CollectionTask{" +
            "taskId=" + taskId +
            ", name=" + name +
            ", policyId=" + policyId +
            ", invokeCode=" + invokeCode +
            ", invokeMethod=" + invokeMethod +
            ", invokeParams=" + invokeParams +
            ", preRunTime=" + preRunTime +
            ", runTime=" + runTime +
            ", finishTime=" + finishTime +
            ", createId=" + createId +
            ", createName=" + createName +
            ", createTime=" + createTime +
            ", runCount=" + runCount +
            ", exMsg=" + exMsg +
        "}";
    }
}
