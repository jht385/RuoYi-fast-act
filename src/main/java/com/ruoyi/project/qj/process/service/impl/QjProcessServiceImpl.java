package com.ruoyi.project.qj.process.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.security.ShiroUtils;
import com.ruoyi.common.utils.text.Convert;
import com.ruoyi.framework.config.RuoYiConfig;
import com.ruoyi.project.activiti.mapper.ProcessWorkMapper;
import com.ruoyi.project.activiti.service.IProcessService;
import com.ruoyi.project.qj.process.domain.QjProcess;
import com.ruoyi.project.qj.process.mapper.QjProcessMapper;
import com.ruoyi.project.qj.process.service.IQjProcessService;
import com.ruoyi.project.system.role.mapper.RoleExMapper;
import com.ruoyi.project.system.user.domain.User;
import com.ruoyi.project.system.user.mapper.UserExMapper;

import cn.hutool.extra.mail.MailUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 请假流程Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-02-03
 */
@Slf4j
@Service
public class QjProcessServiceImpl implements IQjProcessService {
	private static final String processDefinitionKey = "qj";

	@Autowired
	private QjProcessMapper qjProcessMapper;
	@Autowired
	private RuoYiConfig ruoYiConfig;
	@Autowired
	private IProcessService processService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private UserExMapper userExMapper;
	@Autowired
	private RoleExMapper roleExMapper;
	@Autowired
	private ProcessWorkMapper processWorkMapper;

	/**
	 * 查询请假流程
	 * 
	 * @param processId 请假流程主键
	 * @return 请假流程
	 */
	@Override
	public QjProcess selectQjProcessByProcessId(String processId) {
		return qjProcessMapper.selectQjProcessByProcessId(processId);
	}

	/**
	 * 查询请假流程列表
	 * 
	 * @param qjProcess 请假流程
	 * @return 请假流程
	 */
	@Override
	public List<QjProcess> selectQjProcessList(QjProcess qjProcess) {
		return qjProcessMapper.selectQjProcessList(qjProcess);
	}

	/**
	 * 新增请假流程
	 * 
	 * @param qjProcess 请假流程
	 * @return 结果
	 */
	@Override
	public int insertQjProcess(QjProcess qjProcess) {
		qjProcess.setCreateTime(DateUtils.getNowDate());
		return qjProcessMapper.insertQjProcess(qjProcess);
	}

	/**
	 * 修改请假流程
	 * 
	 * @param qjProcess 请假流程
	 * @return 结果
	 */
	@Override
	public int updateQjProcess(QjProcess qjProcess) {
		return qjProcessMapper.updateQjProcess(qjProcess);
	}

	/**
	 * 批量删除请假流程
	 * 
	 * @param processIds 需要删除的请假流程主键
	 * @return 结果
	 */
	@Override
	public int deleteQjProcessByProcessIds(String processIds) {
		return qjProcessMapper.deleteQjProcessByProcessIds(Convert.toStrArray(processIds));
	}

	/**
	 * 删除请假流程信息
	 * 
	 * @param processId 请假流程主键
	 * @return 结果
	 */
	@Override
	public int deleteQjProcessByProcessId(String processId) {
		return qjProcessMapper.deleteQjProcessByProcessId(processId);
	}

	@Transactional
	@Override
	public int processFormStart(QjProcess t) {
		String applyUserId = ShiroUtils.getUserId().toString();
		String businessKey = t.getProcessId().toString(); // 实体类 ID，作为流程的业务 key
		String nextMsg = t.getNextMsg();
		Integer pass = t.getPass();

		Map<String, Object> variables = new HashMap<String, Object>();

		// 系统是把所有流程放到一个菜单，这里用流程的一些关键信息组成名字方便后续查询
		String actInstName = t.getName();

		// start

		// 为了匹配后面的返回，这里没有也要填充
		variables.put("users", ""); // 配置下一节点候选用户，模板写了这里不设置变量会报错
		variables.put("groups", ""); // 配置下一阶段候选组

		ProcessInstance processInstance = processService.simpleStart(applyUserId, businessKey, actInstName,
				processDefinitionKey, variables);

		// n1
		String processInstanceId = processInstance.getId();
		Task task = processService.getProcessInstanceSingleTask(processInstanceId);
		// 这里只是针对任务完成人自己的描述，下面的api更符合于其他人评论这个任务
		// processService.addComment(task.getId(), processInstanceId, "<b
		// style=\"color:green\">" + nextMsg + "</b>");
		task.setDescription("<b style=\"color:green\">" + nextMsg + "</b>");
		processService.saveOrUpdateTask(task); // 无记录添加，有记录更新

		String taskDefinitionKey = task.getTaskDefinitionKey();
		String groups = getGroupByForwardTaskDefinitionKey(taskDefinitionKey);

		if (t.getPass() == 1) { // 这里是发起，无拒绝
			t.setStatus("1");
		}

		variables.clear();
		variables.put("users", ""); // 配置下一节点候选用户，模板写了这里不设置变量会报错
		variables.put("groups", groups); // 配置下一阶段候选组
		variables.put("pass", pass);
		processService.completeTask(task.getId(), applyUserId, variables);

		// 通知逻辑，这里是以ui上选择的人通知，没有按groups通知
		Long[] userIds = t.getNextUser();
		List<User> userList = userExMapper.listUserByUserIds(userIds);
		// List<User> userList = userExMapper.listUserByRoleIds(groups);
		String mailTo = "";
		String title = processDefinitionKey + "评审待处理提醒";
		String content = "名字为：" + actInstName + " 的流程需要处理\n请登录 http://" + ruoYiConfig.getAppServer() + "/ 查看";

		StringBuilder sb = new StringBuilder();
		for (User user : userList) {
			if (!StringUtils.isEmpty(user.getEmail())) {
				sb.append(user.getEmail() + ";");
			}
		}
		mailTo = sb.toString();

		log.debug("邮件记录\n发邮件给：" + mailTo + "\n标题：" + title + "\n内容：" + content);
		if ("pro".equals(ruoYiConfig.getActive())) {
			try {
				MailUtil.send(mailTo, title, content, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		t.setInstanceId(processInstanceId);
		t.setCreateBy(applyUserId);
		t.setCreateTime(DateTime.now().toDate());
		int cnt = qjProcessMapper.insertQjProcess(t);

		return cnt;
	}

	@Override
	public QjProcess selectProcessInfoByProcessId(String processId) {
		return qjProcessMapper.selectQjProcessByProcessId(processId);
	}

	@Transactional
	@Override
	public int handle(QjProcess t) {
		String applyUserId = ShiroUtils.getUserId().toString();
		String nextMsg = t.getNextMsg();
		Integer pass = t.getPass();
		String taskId = t.getTaskId();

		Map<String, Object> variables = new HashMap<String, Object>();

		Task task = processService.getTaskById(taskId);
		String taskDefinitionKey = task.getTaskDefinitionKey();
		String groups = "";

		if (pass == 0) { // 拒绝
			t.setStatus("3");
			task.setDescription("<b style=\"color:red\">" + nextMsg + "</b>");
			processService.saveOrUpdateTask(task);
			variables.put("pass", pass);
			processService.completeTask(task.getId(), applyUserId, variables);
			qjProcessMapper.updateQjProcess(t);
			return 1;
		} else if (pass == 1) { // 通过
			task.setDescription("<b style=\"color:green\">" + nextMsg + "</b>");

			groups = getGroupByForwardTaskDefinitionKey(taskDefinitionKey);
		} else if (pass == 2) { // 返回
			task.setDescription("<b style=\"color:red\">" + nextMsg + "</b>");

			groups = getGroupByReverseTaskDefinitionKey(taskDefinitionKey);
		}
		processService.saveOrUpdateTask(task);

		HistoricVariableInstance historicVariableInstance = historyService.createHistoricVariableInstanceQuery()
				.executionId(task.getProcessInstanceId()).variableName("actInstName").singleResult();
		String id = historicVariableInstance.getId();
		String actInstName = (String) historicVariableInstance.getValue();

		if ("n1".equals(taskDefinitionKey)) {
			qjProcessMapper.updateQjProcess(t); // 返回时要更新
			
			// 如果流程流转中，修改了actInstName关键字则需要更新
			// act_hi_varinst 没找到官方方法更新，只能自己这样更新了
//			variables.clear();
//			variables.put("id", id);
//			actInstName = t.getName(); // 以最新的值设置
//			variables.put("text", actInstName);
//			processWorkMapper.updateActHiVarinstByMap(variables);
			
			actInstName = (String) historicVariableInstance.getValue();
			
			if (pass == 1) { // 完结流程时如果是同意则设置一下对应业务记录的状态
				t.setStatus("2");
				qjProcessMapper.updateQjProcess(t);
			}
		}

		variables.clear();
		variables.put("users", ""); // 配置下一节点候选用户，模板写了这里不设置变量会报错
		variables.put("groups", groups); // 配置下一阶段候选组
		variables.put("pass", pass);
		processService.completeTask(task.getId(), applyUserId, variables);

		// 通知逻辑，这里是以ui上选择的人通知，没有按groups通知
		Long[] userIds = t.getNextUser();
		List<User> userList = userExMapper.listUserByUserIds(userIds);
		// List<User> userList = userExMapper.listUserByRoleIds(groups);
		String mailTo = "";
		String title = processDefinitionKey + "评审待处理提醒";
		String content = "名字为：" + actInstName + " 的流程需要处理\n请登录 http://" + ruoYiConfig.getAppServer() + "/ 查看";

		StringBuilder sb = new StringBuilder();
		for (User user : userList) {
			if (!StringUtils.isEmpty(user.getEmail())) {
				sb.append(user.getEmail() + ";");
			}
		}
		mailTo = sb.toString();

		log.debug("邮件记录\n发邮件给：" + mailTo + "\n标题：" + title + "\n内容：" + content);
		if ("pro".equals(ruoYiConfig.getActive())) {
			try {
				MailUtil.send(mailTo, title, content, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return 1;
	}

	// 正向流转
	public String getGroupByForwardTaskDefinitionKey(String taskDefinitionKey) {
		String groups = "";
		switch (taskDefinitionKey) {
		case "n1":
			groups = roleExMapper.getRoleIdsByRoleKeys("l1");
			break;
		default:
			break;
		}
		return groups;
	}

	// 反向流转
	public String getGroupByReverseTaskDefinitionKey(String taskDefinitionKey) {
		String groups = "";
		switch (taskDefinitionKey) {
		case "n2": // 这里应该通知发起人，知道就行，自行修改
			groups = roleExMapper.getRoleIdsByRoleKeys("common");
			break;
		default:
			break;
		}
		return groups;
	}

	@Transactional
	@Override
	public int processFormEdit(QjProcess t) {
		int cnt = qjProcessMapper.updateQjProcess(t);

		if (cnt > 0) {
			String actInstName = t.getName();
			String currentUserName = ShiroUtils.getSysUser().getUserName();
			String instanceId = t.getInstanceId();

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("procInstId", instanceId);
			map.put("name", "actInstName");
			map.put("text", actInstName);
			processWorkMapper.updateActHiVarinstByMap(map);

			String mailTo = "";
			String title = processDefinitionKey + "评审内容变更提醒";
			String content = "名字为：" + actInstName + " 的流程被 " + currentUserName + " 修改了\n" + "修改意见：" + t.getNextMsg()
					+ "\n" + "请相关人员登录 http://" + ruoYiConfig.getAppServer() + "/ 查看\n相关的备货单注意更新";

			StringBuilder sb = new StringBuilder();
			List<User> userList = processWorkMapper.getProcInstUserByProcInstId(instanceId);
			for (User user : userList) {
				if (!StringUtils.isEmpty(user.getEmail())) {
					sb.append(user.getEmail() + ";");
				}
			}
			mailTo = sb.toString();
			log.debug("邮件记录\n发邮件给：" + mailTo + "\n标题：" + title + "\n内容：" + content);

			if ("pro".equals(ruoYiConfig.getActive())) {
				try {
					MailUtil.send(mailTo, title, content, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return cnt;
	}
}
