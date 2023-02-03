package com.ruoyi.project.activiti.service;

import com.ruoyi.project.activiti.domain.HistoricActivity;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface IProcessService {

	List<HistoricActivity> selectHistoryList(String processInstanceId, HistoricActivity historicActivity);

	ProcessInstance simpleStart(String applyUserId, String businessKey, String actInstName, String processDefinitionKey,
			Map<String, Object> variables);
	
	Task getProcessInstanceSingleTask(String processInstanceId);
	
	Comment addComment(String taskId, String processInstanceId, String message);
	
	void saveOrUpdateTask(Task task);
	
	void completeTask(String taskId, String applyUserId, Map<String, Object> variables);

	ProcessInstance submitApply(String applyUserId, String businessKey, String actInstName, String itemConent,
			String processDefinitionKey, Map<String, Object> variables);

	List<Task> findTodoTasks(String userId, String key);

	List<HistoricTaskInstance> findDoneTasks(String userId, String key);
	
	Task getTaskById(String taskId);
	
	void complete(String taskId, String instanceId, String itemName, String itemContent, String module,
			Map<String, Object> variables, HttpServletRequest request);

	void delegate(String taskId, String fromUser, String delegateToUser);

	void cancelApply(String instanceId, String deleteReason);

	void suspendOrActiveApply(String instanceId, String suspendState);

	String findBusinessKeyByInstanceId(String instanceId);

	
}
