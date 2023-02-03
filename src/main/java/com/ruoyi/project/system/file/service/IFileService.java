package com.ruoyi.project.system.file.service;

import java.util.List;

import com.ruoyi.project.system.file.domain.File;
import com.ruoyi.project.system.file.domain.MulFile;

/**
 * 文件记录Service接口
 * 
 * @author ruoyi
 * @date 2022-01-26
 */
public interface IFileService 
{
    /**
     * 查询文件记录
     * 
     * @param fileId 文件记录主键
     * @return 文件记录
     */
    public File selectFileByFileId(String fileId);

    /**
     * 查询文件记录列表
     * 
     * @param file 文件记录
     * @return 文件记录集合
     */
    public List<File> selectFileList(File file);

    /**
     * 新增文件记录
     * 
     * @param file 文件记录
     * @return 结果
     */
    public int insertFile(File file);

    /**
     * 修改文件记录
     * 
     * @param file 文件记录
     * @return 结果
     */
    public int updateFile(File file);

    /**
     * 批量删除文件记录
     * 
     * @param fileIds 需要删除的文件记录主键集合
     * @return 结果
     */
    public int deleteFileByFileIds(String fileIds);

    /**
     * 删除文件记录信息
     * 
     * @param fileId 文件记录主键
     * @return 结果
     */
    public int deleteFileByFileId(String fileId);

	public int BizDeleteFileByFileIds(String ids);
	
	public int BizDeleteFileByFileIds(String[] ids);
	
	public int BizDeleteFileByFileIds(List<String> ids);

	public int uploadFiles(MulFile mulfile);

	public int copyFileByRef(File file);

	public int fileRefresh(File file);

}
