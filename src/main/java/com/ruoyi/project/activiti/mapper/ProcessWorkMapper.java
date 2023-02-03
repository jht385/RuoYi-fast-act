package com.ruoyi.project.activiti.mapper;

import java.util.List;
import java.util.Map;

import com.ruoyi.project.activiti.domain.ProcessWork;
import com.ruoyi.project.activiti.domain.ProcinstVO;
import com.ruoyi.project.activiti.domain.TaskVO;
import com.ruoyi.project.system.user.domain.User;

public interface ProcessWorkMapper {
    public List<ProcessWork> newestVersionList(ProcessWork processWork);

	public List<TaskVO> todoList(TaskVO taskVO);

	public Long todoListSize(TaskVO taskVO);
	
	public List<ProcinstVO> doneList(ProcinstVO taskVO);
	
	int updateActHiVarinstByMap(Map<String, Object> map);

	public List<User> getProcInstUserByProcInstId(String procInstId);
}
