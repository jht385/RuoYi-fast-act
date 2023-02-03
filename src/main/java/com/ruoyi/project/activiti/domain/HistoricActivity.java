package com.ruoyi.project.activiti.domain;

import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntityImpl;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoricActivity extends HistoricActivityInstanceEntityImpl {
	private static final long serialVersionUID = 1L;

	/** 审批批注 */
	private String comment;

	/** 办理人姓名 */
	private String assigneeName;
	
	private String description;

}
