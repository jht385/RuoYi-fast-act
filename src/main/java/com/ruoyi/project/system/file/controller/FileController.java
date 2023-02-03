package com.ruoyi.project.system.file.controller;

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

import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.project.system.file.domain.File;
import com.ruoyi.project.system.file.domain.MulFile;
import com.ruoyi.project.system.file.service.IFileService;
import com.ruoyi.project.tool.hutool.IdGeneratorSnowflake;

/**
 * 文件记录Controller
 * 
 * @author ruoyi
 * @date 2022-01-26
 */
@Controller
@RequestMapping("/system/file")
public class FileController extends BaseController
{
    private String prefix = "system/file";

    @Autowired
    private IFileService fileService;
    @Autowired
    private IdGeneratorSnowflake idGeneratorSnowflake;

    @RequiresPermissions("system:file:view")
    @GetMapping()
    public String file()
    {
        return prefix + "/file";
    }
    
    @GetMapping("/multipleFile")
    public String multipleFile(String ref, String refId, ModelMap mmap)
    {
    	mmap.put("ref", ref);
    	mmap.put("refId", refId);
    	return prefix + "/multipleFile";
    }
    
    @PostMapping("/multipleFile")
    @ResponseBody
    public AjaxResult uploadFiles(MulFile mulfile) throws Exception
    {
    	return toAjax(fileService.uploadFiles(mulfile));
    }

    /**
     * 查询文件记录列表
     */
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(File file)
    {
        startPage();
        List<File> list = fileService.selectFileList(file);
        return getDataTable(list);
    }
    
    /**
     * 导出文件记录列表
     */
    @RequiresPermissions("system:file:export")
    @Log(title = "文件记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(File file)
    {
        List<File> list = fileService.selectFileList(file);
        ExcelUtil<File> util = new ExcelUtil<File>(File.class);
        return util.exportExcel(list, "文件记录数据");
    }

    /**
     * 新增文件记录
     */
    @GetMapping("/add")
    public String add(ModelMap mmap)
    {
    	mmap.put("fileId", idGeneratorSnowflake.nextId() + "");
        return prefix + "/add";
    }

    /**
     * 新增保存文件记录
     */
    @RequiresPermissions("system:file:add")
    @Log(title = "文件记录", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(File file)
    {
        return toAjax(fileService.insertFile(file));
    }

    /**
     * 修改文件记录
     */
    @RequiresPermissions("system:file:edit")
    @GetMapping("/edit/{fileId}")
    public String edit(@PathVariable("fileId") String fileId, ModelMap mmap)
    {
        File file = fileService.selectFileByFileId(fileId);
        mmap.put("file", file);
        return prefix + "/edit";
    }

    /**
     * 修改保存文件记录
     */
    @RequiresPermissions("system:file:edit")
    @Log(title = "文件记录", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(File file)
    {
        return toAjax(fileService.updateFile(file));
    }

    /**
     * 删除文件记录
     */
    @Log(title = "文件记录", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(fileService.deleteFileByFileIds(ids));
    }
    
    /**
     * 删除文件记录
     */
    @Log(title = "文件记录", businessType = BusinessType.DELETE)
    @PostMapping( "/bizRemove")
    @ResponseBody
    public AjaxResult bizRemove(String ids)
    {
        return toAjax(fileService.BizDeleteFileByFileIds(ids));
    }
    
    @Log(title = "文件复制", businessType = BusinessType.INSERT)
	@ResponseBody
	@PostMapping("/copyFileByRef")
	public Object copyFileByRef(File file) {
		return toAjax(fileService.copyFileByRef(file));
	}
    
    @GetMapping("/fileRefresh")
    public String fileRefresh(String oldFileId, String newRef, ModelMap mmap)
    {
    	mmap.put("oldFileId", oldFileId);
    	mmap.put("newRef", newRef);
    	return prefix + "/fileRefresh";
    }
    
    @PostMapping("/fileRefresh")
    @ResponseBody
    public AjaxResult fileRefresh(File file) throws Exception
    {
    	return toAjax(fileService.fileRefresh(file));
    }
}
