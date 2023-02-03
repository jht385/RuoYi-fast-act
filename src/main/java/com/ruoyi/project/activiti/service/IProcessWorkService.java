package com.ruoyi.project.activiti.service;

import java.util.List;

import com.github.pagehelper.Page;
import com.ruoyi.project.activiti.domain.ProcessWork;
import com.ruoyi.project.activiti.domain.ProcinstVO;
import com.ruoyi.project.activiti.domain.TaskVO;
import com.ruoyi.project.system.dept.domain.Dept;
import com.ruoyi.project.system.user.domain.User;

public interface IProcessWorkService {

	public Page<ProcessWork> newestVersionList(ProcessWork processWork);

	public String getFormKey(String procDefId, String taskDefKey);

	public List<Dept> listDept4Select(Dept dept);

	public List<User> listUser4Select(User user);

	public Page<TaskVO> todoList(TaskVO taskVO);

	public Long todoListSize(TaskVO taskVO);
	
	public Page<ProcinstVO> doneList(ProcinstVO procinstVO);


}
