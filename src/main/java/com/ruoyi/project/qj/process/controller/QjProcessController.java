package com.ruoyi.project.qj.process.controller;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.project.activiti.service.IProcessWorkService;
import com.ruoyi.project.qj.process.domain.QjProcess;
import com.ruoyi.project.qj.process.service.IQjProcessService;
import com.ruoyi.project.system.user.domain.User;
import com.ruoyi.project.tool.hutool.IdGeneratorSnowflake;

/**
 * 请假流程Controller
 * 
 * @author ruoyi
 * @date 2023-02-03
 */
@Controller
@RequestMapping("/qj/process")
public class QjProcessController extends BaseController
{
    private String prefix = "qj/process";

    @Autowired
    private IQjProcessService qjProcessService;
    @Autowired
    private IdGeneratorSnowflake idGeneratorSnowflake;
    @Autowired
    private IProcessWorkService processWorkService;
    
    @GetMapping("/processForm")
    public String processForm(String procInstId, String taskId, String processId, String name, String version, String curTaskDefKey, String action, String node, ModelMap mmap)
    {
    	if("".equals(action)) {
    		action = "new";
    	}
    	mmap.put("users", processWorkService.listUser4Select(new User()));
    	
    	if (StringUtils.isEmpty(processId)) {
    		mmap.put("processId", idGeneratorSnowflake.nextId() + "");
		}else {
			QjProcess beanInfo = qjProcessService.selectProcessInfoByProcessId(processId);
			
			mmap.put("processId", processId);
			mmap.put("procInstId", procInstId);
			mmap.put("taskId", taskId);
			mmap.put("beanInfo", beanInfo);
			mmap.put("node", curTaskDefKey);
		}
    	
    	mmap.put("name", name);
    	// version控制版本，居然看是从@RequestMapping还是@GetMapping后面+数字，还是在方法里if判断version
    	mmap.put("version", version);
    	mmap.put("action", action);
    	mmap.put("node", node);
        return prefix + "/processForm";
    }
    
    @Log(title = "processFormStart", businessType = BusinessType.INSERT)
    @PostMapping("/processFormStart")
    @ResponseBody
    public AjaxResult processFormStart(QjProcess qjProcess)
    {
        return toAjax(qjProcessService.processFormStart(qjProcess));
    }

    @Log(title = "handle", businessType = BusinessType.INSERT)
    @PostMapping("/handle")
    @ResponseBody
    public AjaxResult handle(QjProcess qjProcess)
    {
        return toAjax(qjProcessService.handle(qjProcess));
    }
    
    @Log(title = "processFormEdit", businessType = BusinessType.UPDATE)
    @PostMapping("/processFormEdit")
    @ResponseBody
    public AjaxResult processFormEdit(QjProcess qjProcess)
    {
        return toAjax(qjProcessService.processFormEdit(qjProcess));
    }

    @RequiresPermissions("qj:process:view")
    @GetMapping()
    public String process()
    {
        return prefix + "/process";
    }

    /**
     * 查询请假流程列表
     */
    @RequiresPermissions("qj:process:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(QjProcess qjProcess)
    {
        startPage();
        List<QjProcess> list = qjProcessService.selectQjProcessList(qjProcess);
        return getDataTable(list);
    }

    /**
     * 导出请假流程列表
     */
    @RequiresPermissions("qj:process:export")
    @Log(title = "请假流程", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(QjProcess qjProcess)
    {
        List<QjProcess> list = qjProcessService.selectQjProcessList(qjProcess);
        ExcelUtil<QjProcess> util = new ExcelUtil<QjProcess>(QjProcess.class);
        return util.exportExcel(list, "请假流程数据");
    }

    /**
     * 新增请假流程
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存请假流程
     */
    @RequiresPermissions("qj:process:add")
    @Log(title = "请假流程", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(QjProcess qjProcess)
    {
        return toAjax(qjProcessService.insertQjProcess(qjProcess));
    }

    /**
     * 修改请假流程
     */
    @RequiresPermissions("qj:process:edit")
    @GetMapping("/edit/{processId}")
    public String edit(@PathVariable("processId") String processId, ModelMap mmap)
    {
        QjProcess qjProcess = qjProcessService.selectQjProcessByProcessId(processId);
        mmap.put("qjProcess", qjProcess);
        return prefix + "/edit";
    }

    /**
     * 修改保存请假流程
     */
    @RequiresPermissions("qj:process:edit")
    @Log(title = "请假流程", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(QjProcess qjProcess)
    {
        return toAjax(qjProcessService.updateQjProcess(qjProcess));
    }

    /**
     * 删除请假流程
     */
    @RequiresPermissions("qj:process:remove")
    @Log(title = "请假流程", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(qjProcessService.deleteQjProcessByProcessIds(ids));
    }
}
