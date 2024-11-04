package com.ruoyi.project.activiti.controller;

import java.awt.Color;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.image.ProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruoyi.common.utils.security.ShiroUtils;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.project.activiti.config.ICustomProcessDiagramGenerator;
import com.ruoyi.project.activiti.config.WorkflowConstants;
import com.ruoyi.project.activiti.domain.HistoricActivity;
import com.ruoyi.project.activiti.service.IProcessService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/process")
public class ProcessController extends BaseController {

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private ProcessEngine processEngine;

	@Autowired
	private IProcessService processService;

	@Autowired
	private RuntimeService runtimeService;

	/**
	 * 加载审批历史弹窗
	 */
	@GetMapping("/historyList/{instanceId}")
	public String historyList(@PathVariable("instanceId") String instanceId, ModelMap mmap) {
		mmap.put("instanceId", instanceId);
		return "activiti/historyList";
	}

	/**
	 * 审批历史列表
	 */
	@PostMapping("/listHistory")
	@ResponseBody
	public TableDataInfo listHistory(String instanceId, HistoricActivity historicActivity) {
		startPage();
		List<HistoricActivity> list = processService.selectHistoryList(instanceId, historicActivity);
		return getDataTable(list);
	}

	@RequestMapping(value = "/read-resource")
	public void readResource(String pProcessInstanceId, HttpServletResponse response) throws Exception {
		// 设置页面不缓存
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		try {
			HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
					.processInstanceId(pProcessInstanceId).singleResult(); // 获取历史流程实例
			List<HistoricActivityInstance> historicActivityInstances = historyService
					.createHistoricActivityInstanceQuery().processInstanceId(pProcessInstanceId)
					.orderByHistoricActivityInstanceId().asc().list();
			List<String> highLightedActivitiIds = new ArrayList<>();
			for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
				highLightedActivitiIds.add(historicActivityInstance.getActivityId());
			}
			ProcessDiagramGenerator processDiagramGenerator = processEngine.getProcessEngineConfiguration()
					.getProcessDiagramGenerator();
			BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());
			List<String> highLightedFlowIds = getHighLightedFlows(bpmnModel, historicActivityInstances);// 高亮流程已发生流转的线id集合
			InputStream imageStream = processDiagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivitiIds,
					highLightedFlowIds, "宋体", "微软雅黑", "黑体", null, 1.0);
			
			// 这个有问题，在服务器显示不出来
//			ICustomProcessDiagramGenerator diagramGenerator = (ICustomProcessDiagramGenerator) processEngine
//					.getProcessEngineConfiguration().getProcessDiagramGenerator();
//			Set<String> currIds = runtimeService.createExecutionQuery().processInstanceId(pProcessInstanceId).list()
//					.stream().map(e -> e.getActivityId()).collect(Collectors.toSet());
//			InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivitiIds,
//					highLightedFlowIds, "宋体", "宋体", "宋体", null, 1.0,
//					new Color[] { WorkflowConstants.COLOR_NORMAL, WorkflowConstants.COLOR_CURRENT }, currIds);

			byte[] b = new byte[1024];
			int len;
			while ((len = imageStream.read(b, 0, 1024)) != -1) {
				response.getOutputStream().write(b, 0, len);
			}
		} catch (Exception e) {
			log.info("processInstanceId" + pProcessInstanceId + "生成流程图失败，原因：" + e.getMessage());
		}
	}

	private List<String> getHighLightedFlows(BpmnModel bpmnModel,
			List<HistoricActivityInstance> historicActivityInstances) {
		List<String> highLightedFlowIds = new ArrayList<>();// 高亮流程已发生流转的线id集合
		List<FlowNode> historicActivityNodes = new ArrayList<>();// 全部活动节点
		List<HistoricActivityInstance> finishedActivityInstances = new ArrayList<>();// 已完成的历史活动节点
		for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) { // 填充已完成的历史活动节点
			FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess()
					.getFlowElement(historicActivityInstance.getActivityId(), true);
			historicActivityNodes.add(flowNode);
			if (historicActivityInstance.getEndTime() != null) {
				finishedActivityInstances.add(historicActivityInstance);
			}
		}
		FlowNode currentFlowNode = null;
		FlowNode targetFlowNode = null;
		for (HistoricActivityInstance currentActivityInstance : finishedActivityInstances) {// 遍历已完成的活动实例，从每个实例的outgoingFlows中找到已执行的
			// 获得当前活动对应的节点信息及outgoingFlows信息
			currentFlowNode = (FlowNode) bpmnModel.getMainProcess()
					.getFlowElement(currentActivityInstance.getActivityId(), true);
			List<SequenceFlow> sequenceFlows = currentFlowNode.getOutgoingFlows();

			/**
			 * 遍历outgoingFlows并找到已已流转的 满足如下条件认为已已流转：
			 * 1.当前节点是并行网关或兼容网关，则通过outgoingFlows能够在历史活动中找到的全部节点均为已流转
			 * 2.当前节点是以上两种类型之外的，通过outgoingFlows查找到的时间最早的流转节点视为有效流转
			 */
			if ("parallelGateway".equals(currentActivityInstance.getActivityType())
					|| "inclusiveGateway".equals(currentActivityInstance.getActivityType())) {
				// 遍历历史活动节点，找到匹配流程目标节点的
				for (SequenceFlow sequenceFlow : sequenceFlows) {
					targetFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(sequenceFlow.getTargetRef(),
							true);
					if (historicActivityNodes.contains(targetFlowNode)) {
						highLightedFlowIds.add(targetFlowNode.getId());
					}
				}
			} else {
				List<Map<String, Object>> tempMapList = new ArrayList<>();
				for (SequenceFlow sequenceFlow : sequenceFlows) {
					for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
						if (historicActivityInstance.getActivityId().equals(sequenceFlow.getTargetRef())) {
							Map<String, Object> map = new HashMap<>();
							map.put("highLightedFlowId", sequenceFlow.getId());
							map.put("highLightedFlowStartTime", historicActivityInstance.getStartTime().getTime());
							tempMapList.add(map);
						}
					}
				}

				if (tempMapList != null && tempMapList.size() != 0) {// 遍历匹配的集合，取得开始时间最早的一个
					long earliestStamp = 0L;
					String highLightedFlowId = null;
					for (Map<String, Object> map : tempMapList) {
						long highLightedFlowStartTime = Long.valueOf(map.get("highLightedFlowStartTime").toString());
						if (earliestStamp == 0 || earliestStamp >= highLightedFlowStartTime) {
							highLightedFlowId = map.get("highLightedFlowId").toString();
							earliestStamp = highLightedFlowStartTime;
						}
					}
					highLightedFlowIds.add(highLightedFlowId);
				}
			}
		}
		return highLightedFlowIds;
	}

	@GetMapping("/processImg/{instanceId}")
	public String processImg(@PathVariable("instanceId") String instanceId, ModelMap mmap) {
		mmap.put("instanceId", instanceId);
		return "activiti/processImg";
	}

	@PostMapping("/delegate")
	@ResponseBody
	public AjaxResult delegate(String taskId, String delegateToUser) {
		processService.delegate(taskId, ShiroUtils.getLoginName(), delegateToUser);
		return success();
	}

	@PostMapping("/cancelApply")
	@ResponseBody
	public AjaxResult cancelApply(String instanceId) {
		processService.cancelApply(instanceId, "用户撤销");
		return success();
	}

	@PostMapping("/suspendOrActiveApply")
	@ResponseBody
	public AjaxResult suspendOrActiveApply(String instanceId, String suspendState) {
		processService.suspendOrActiveApply(instanceId, suspendState);
		return success();
	}

}
