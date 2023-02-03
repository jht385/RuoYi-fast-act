package com.ruoyi.project.activiti.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.project.activiti.domain.ProcessWork;
import com.ruoyi.project.activiti.domain.ProcinstVO;
import com.ruoyi.project.activiti.domain.TaskVO;
import com.ruoyi.project.activiti.service.IProcessWorkService;
import com.ruoyi.project.system.dept.domain.Dept;
import com.ruoyi.project.system.user.domain.User;


@Controller
@RequestMapping("/activiti")
public class ProcessWorkController extends BaseController {

    @Autowired
    private IProcessWorkService processWorkService;

    @GetMapping("/newestVersion")
    public String processDefinition() {
        return "activiti/newestVersion";
    }

    @PostMapping("/newestVersionList")
    @ResponseBody
    public TableDataInfo newestVersionList(ProcessWork processWork) {
    	startPage();
        List<ProcessWork> list = processWorkService.newestVersionList(processWork);
        return getDataTable(list);
    }
    
    @GetMapping("/todo")
    public String todoList(){
    	return "activiti/todo";
    }
    
    @PostMapping("/todoList")
    @ResponseBody
    public TableDataInfo todoList(TaskVO taskVO) {
    	startPage();
        List<TaskVO> list = processWorkService.todoList(taskVO);
        return getDataTable(list);
    }
    
    @PostMapping("/todoListSize")
    @ResponseBody
    public Object todoListSize(TaskVO taskVO) {
    	Long total = processWorkService.todoListSize(taskVO);
    	return success(total.intValue());
    }
    
    @GetMapping("/done")
    public String done(){
    	return "activiti/done";
    }
    
    @PostMapping("/doneList")
    @ResponseBody
    public TableDataInfo doneList(ProcinstVO procinstVO) {
    	startPage();
    	List<ProcinstVO> list = processWorkService.doneList(procinstVO);
    	return getDataTable(list);
    }
    
	@GetMapping("/processForm")
	public void actForm(String id, String name, String version, HttpServletResponse response)
			throws IOException {
		String formKey = processWorkService.getFormKey(id, null);
		StringBuilder url = new StringBuilder(formKey);
//		url.append("&id=" + id); // url不能带":"
		url.append("?name=" + URLEncoder.encode(name, "UTF-8"));
		url.append("&version=" + version);
		url.append("&action=new");
		url.append("&node=n1"); // 获取不到暂时都按第一节点命名来
		response.sendRedirect(url.toString());
	}
	
    @GetMapping("/nextMsg")
    public String nextMsg(Integer showYn, Integer deptFlag, Integer userFlag, ModelMap mmap){
    	mmap.put("showYn", showYn);
    	mmap.put("deptFlag", deptFlag);
    	mmap.put("userFlag", userFlag);
    	if(deptFlag == 1) { // 查询所有部门
    		mmap.put("depts", processWorkService.listDept4Select(new Dept()));
    	}
    	if(userFlag == 1) { // 查询所有用户
    		mmap.put("users", processWorkService.listUser4Select(new User()));
    	}
        return "activiti/nextMsg";
    }

}
