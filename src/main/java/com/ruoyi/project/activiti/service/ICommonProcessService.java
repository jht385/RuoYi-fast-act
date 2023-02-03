package com.ruoyi.project.activiti.service;

public interface ICommonProcessService<T> {
	int processFormStart(T t);

	T selectProcessInfoByProcessId(String processId);

	int handle(T t);

	int processFormEdit(T t);
}
