package com.ruoyi.project.activiti.domain;

import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ProcessDefinition extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private String id;

	@Excel(name = "流程名称")
	private String name;

	@Excel(name = "流程KEY")
	private String key;

	@Excel(name = "流程版本")
	private int version;

	@Excel(name = "所属分类")
	private String category;

	@Excel(name = "流程描述")
	private String description;

	private String deploymentId;

	@Excel(name = "部署时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
	private Date deploymentTime;

	@Excel(name = "流程图")
	private String diagramResourceName;

	@Excel(name = "流程定义")
	private String resourceName;

	/** 流程实例状态 1 激活 2 挂起 */
	private String suspendState;

	private String suspendStateName;

}
