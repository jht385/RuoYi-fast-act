package com.ruoyi.project.activiti.domain;

import lombok.Data;

@Data
public class ProcinstVO {
	private String id;
	private String procInstId;
	private String businessKey;
	private String procDefId;
	private String startTime;
	private String endTime;
	private String actInstName;
	private String curtaskId;
	private String curTaskName;
	private String curTaskDefKey;
	private String curTaskFormKey;
	private String pass;
	private String actKey;
	private String actName;
	//
	private Long userId;
}
