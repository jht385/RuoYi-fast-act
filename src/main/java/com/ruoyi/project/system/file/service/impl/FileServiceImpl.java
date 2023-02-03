package com.ruoyi.project.system.file.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.security.ShiroUtils;
import com.ruoyi.common.utils.text.Convert;
import com.ruoyi.framework.config.RuoYiConfig;
import com.ruoyi.project.system.file.domain.File;
import com.ruoyi.project.system.file.domain.MulFile;
import com.ruoyi.project.system.file.mapper.FileMapper;
import com.ruoyi.project.system.file.service.IFileService;
import com.ruoyi.project.tool.hutool.IdGeneratorSnowflake;

import cn.hutool.core.date.DateTime;

/**
 * 文件记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-01-26
 */
@Service
public class FileServiceImpl implements IFileService {
	@Autowired
	private FileMapper fileMapper;
	@Autowired
	private IdGeneratorSnowflake idGeneratorSnowflake;

	/**
	 * 查询文件记录
	 * 
	 * @param fileId 文件记录主键
	 * @return 文件记录
	 */
	@Override
	public File selectFileByFileId(String fileId) {
		return fileMapper.selectFileByFileId(fileId);
	}

	/**
	 * 查询文件记录列表
	 * 
	 * @param file 文件记录
	 * @return 文件记录
	 */
	@Override
	public List<File> selectFileList(File file) {
		return fileMapper.selectFileList(file);
	}

	/**
	 * 新增文件记录
	 * 
	 * @param file 文件记录
	 * @return 结果
	 */
	@Override
	public int insertFile(File file) {
		file.setCreateTime(DateUtils.getNowDate());
		file.setCreateBy(ShiroUtils.getLoginName());
		return fileMapper.insertFile(file);
	}

	/**
	 * 修改文件记录
	 * 
	 * @param file 文件记录
	 * @return 结果
	 */
	@Override
	public int updateFile(File file) {
		file.setCreateTime(DateUtils.getNowDate());
		file.setCreateBy(ShiroUtils.getLoginName());
		return fileMapper.updateFile(file);
	}

	/**
	 * 批量删除文件记录
	 * 
	 * @param fileIds 需要删除的文件记录主键
	 * @return 结果
	 */
	@Override
	public int deleteFileByFileIds(String fileIds) {
		return fileMapper.deleteFileByFileIds(Convert.toStrArray(fileIds));
	}

	/**
	 * 删除文件记录信息
	 * 
	 * @param fileId 文件记录主键
	 * @return 结果
	 */
	@Override
	public int deleteFileByFileId(String fileId) {
		return fileMapper.deleteFileByFileId(fileId);
	}

	/**
	 * 业务删除，删除记录的同时也删除文件
	 */
	@Override
	public int BizDeleteFileByFileIds(String ids) {
		String[] idArr = Convert.toStrArray(ids);
		return BizDeleteFileByFileIds(idArr);
	}

	@Override
	public int BizDeleteFileByFileIds(List<String> ids) {
		String[] idArr = (String[]) (ids.toArray(new String[ids.size()]));
		return BizDeleteFileByFileIds(idArr);
	}

	@Override
	public int BizDeleteFileByFileIds(String[] ids) {
		int i = 0;
		for (; i < ids.length; i++) {
			String fileId = ids[i];
			File file = fileMapper.selectFileByFileId(fileId);

			if ("0".equals(file.getIsCopy())) { // 非拷贝的删除物理文件
				String url = file.getUrl();
				String localPath = RuoYiConfig.getProfile();
				// 数据库资源地址
				String downloadPath = localPath + StringUtils.substringAfter(url, Constants.RESOURCE_PREFIX);
				java.io.File temp = new java.io.File(downloadPath);
				if (temp.exists()) {
					temp.delete();
				}
			}

			fileMapper.deleteFileByFileId(fileId);
		}
		return i;
	}

	@Override
	public int uploadFiles(MulFile mulfile) {
		System.out.println(mulfile.getFilesStr());
		List<File> fileList = JSONObject.parseArray(mulfile.getFilesStr(), File.class);
		CopyOnWriteArrayList<File> copyFileList = new CopyOnWriteArrayList<File>(fileList);
		for (File copyFile : copyFileList) {
			copyFile.setFileId(idGeneratorSnowflake.nextId() + "");
			copyFile.setCreateTime(DateUtils.getNowDate());
			copyFile.setCreateBy(ShiroUtils.getLoginName());
		}
		int i = fileMapper.batchAddFile(copyFileList.subList(0, copyFileList.size()));
		return i;
	}

	@Override
	public int copyFileByRef(File file) {
		List<File> files = fileMapper.selectFileList(file);
		for (File temp : files) {
			File newFile = new File();
			BeanUtils.copyProperties(temp, newFile);
			newFile.setFileId(idGeneratorSnowflake.nextId() + "");
			newFile.setRef(file.getNewRef());
			newFile.setRefId(file.getNewRefId());
			newFile.setIsCopy("1");
			fileMapper.insertFile(newFile);
		}
		return files.size();
	}

	@Override
	public int fileRefresh(File file) {
		String oldFileId = file.getFileId();
		File oldFile = fileMapper.selectFileByFileId(oldFileId);
		String ref = oldFile.getRef();
		String refId = oldFile.getRefId();
		oldFile.setRef(file.getRef());
		fileMapper.updateFile(oldFile);
		
		String newFileId = idGeneratorSnowflake.nextId() + "";
		file.setFileId(newFileId);
		file.setRef(ref);
		file.setRefId(refId);
		file.setCreateBy(ShiroUtils.getLoginName());
		file.setCreateTime(DateTime.now());
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("oldFileId", oldFileId);
		map.put("newFileId", newFileId);
		return fileMapper.insertFile(file);
	}

}
