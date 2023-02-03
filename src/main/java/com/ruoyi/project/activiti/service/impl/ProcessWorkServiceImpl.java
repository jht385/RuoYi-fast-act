package com.ruoyi.project.activiti.service.impl;

import java.util.List;

import org.activiti.engine.FormService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.security.ShiroUtils;
import com.ruoyi.project.activiti.domain.ProcessWork;
import com.ruoyi.project.activiti.domain.ProcinstVO;
import com.ruoyi.project.activiti.domain.TaskVO;
import com.ruoyi.project.activiti.mapper.ProcessWorkMapper;
import com.ruoyi.project.activiti.service.IProcessWorkService;
import com.ruoyi.project.system.dept.domain.Dept;
import com.ruoyi.project.system.dept.mapper.DeptExMapper;
import com.ruoyi.project.system.user.domain.User;
import com.ruoyi.project.system.user.mapper.UserExMapper;

@Service
@Transactional
public class ProcessWorkServiceImpl implements IProcessWorkService {

	@Autowired
	private ProcessWorkMapper processWorkMapper;
	@Autowired
	private DeptExMapper deptExMapper;
	@Autowired
	private UserExMapper userExMapper;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private FormService formService;

	/**
	 * 分页查询流程定义文件
	 * 
	 * @return
	 */
	@Override
	public Page<ProcessWork> newestVersionList(ProcessWork processWork) {
		Long userId = ShiroUtils.getUserId();
		if (userId != 1l) { // 不是管理员按照用户id->role->act权限，管理员过掉这个条件
			processWork.setUserId(userId);
		}

		Page<ProcessWork> processDefinitionList = (Page<ProcessWork>) processWorkMapper.newestVersionList(processWork);
		return processDefinitionList;
	}
	
	@Override
	public Page<TaskVO> todoList(TaskVO taskVO) {
		Long userId = ShiroUtils.getUserId();
		if (userId != 1l) { // 不是管理员按照用户id->role->act权限，管理员过掉这个条件
			taskVO.setUserId(userId);
		}

		Page<TaskVO> taskVOList = (Page<TaskVO>) processWorkMapper.todoList(taskVO);
		return taskVOList;
	}
	
	@Override
	public Long todoListSize(TaskVO taskVO) {
		Long userId = ShiroUtils.getUserId();
		if (userId != 1l) { // 不是管理员按照用户id->role->act权限，管理员过掉这个条件
			taskVO.setUserId(userId);
		}
		Long cnt = processWorkMapper.todoListSize(taskVO);
		return cnt;
	}
	
	@Override
	public Page<ProcinstVO> doneList(ProcinstVO procinstVO) {
		// 集成这里是所有人都可见
		/*
		Long userId = ShiroUtils.getUserId();
		if (userId != 1l) { // 不是管理员按照用户id->role->act权限，管理员过掉这个条件
			procinstVO.setUserId(userId);
		}
		*/
		
		Page<ProcinstVO> procinstVOs = (Page<ProcinstVO>) processWorkMapper.doneList(procinstVO);
		return procinstVOs;
	}

	@Override
	public String getFormKey(String procDefId, String taskDefKey) {
		String formKey = "";
		if (StringUtils.isNotBlank(procDefId)) {
			if (StringUtils.isBlank(formKey)) {
				formKey = formService.getStartFormKey(procDefId);
			} else {
				// 这里是需要先以任务是否有指定formkey来跳转，没有设置才按总的模板formkey
				if (StringUtils.isNotBlank(taskDefKey)) {
					try {
						// 实际不一定会为某个task设置formkey，但是他这里不能压制异常会打印一次异常，就很烦
						formKey = formService.getTaskFormKey(procDefId, taskDefKey);
					} catch (Exception e) {
						formKey = "";
					}
				}
				if (StringUtils.isBlank(formKey)) {
					formKey = "/404";
				}
			}
		}
		return formKey;
	}

	@Override
	public List<Dept> listDept4Select(Dept dept) {
		return deptExMapper.listDept4Select(dept);
	}

	@Override
	public List<User> listUser4Select(User user) {
		return userExMapper.listUser4Select(user);
	}

}
