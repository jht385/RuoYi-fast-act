package com.ruoyi.project.qj.process.domain;

import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.ruoyi.framework.web.domain.BaseEntity;

/**
 * 请假流程对象 qj_process
 * 
 * @author ruoyi
 * @date 2023-02-03
 */
@Getter
@Setter
@ToString
public class QjProcess extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 流程id */
    private String processId;

    /** 流程实例id */
    @Excel(name = "流程实例id")
    private String instanceId;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 名称 */
    @Excel(name = "名称")
    private String name;
    
    //
    private String taskId;
    private String node;
    private Integer pass;
    private String nextMsg;
    private Long[] nextDept;
    private Long[] nextUser;
    private String formKey;
    private String descriptions;

}
