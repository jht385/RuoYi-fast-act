package com.ruoyi.project.activiti.domain;

import java.util.Date;

import lombok.Data;

@Data
public class TaskVO {
	private String curtaskId;
	private String executionId;
	private String procInstId;
	private String processDefinitionId;
	private String curTaskName;
	private String curTaskDefKey;
	private String priority;
	private Date createTime;
	private Integer suspensionState;
	private String curTaskFormKey;
	private String actInstName;
	private String reviewId;
	//
	private Long userId;
	private String actName;
}
