package com.ruoyi.project.system.file.mapper;

import java.util.List;
import java.util.Map;

import com.ruoyi.project.system.file.domain.File;

/**
 * 文件记录Mapper接口
 * 
 * @author ruoyi
 * @date 2022-01-26
 */
public interface FileMapper 
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
     * 删除文件记录
     * 
     * @param fileId 文件记录主键
     * @return 结果
     */
    public int deleteFileByFileId(String fileId);

    /**
     * 批量删除文件记录
     * 
     * @param fileIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFileByFileIds(String[] fileIds);

	public int batchAddFile(List<File> copyFileList);

	public int copyFileByRef(Map<String, Object> map);

	public int batchAuthUser(List<Map<String, Object>> authUserlist);

}
