package com.ruoyi.project.qj.process.mapper;

import java.util.List;
import com.ruoyi.project.qj.process.domain.QjProcess;

/**
 * 请假流程Mapper接口
 * 
 * @author ruoyi
 * @date 2023-02-03
 */
public interface QjProcessMapper 
{
    /**
     * 查询请假流程
     * 
     * @param processId 请假流程主键
     * @return 请假流程
     */
    public QjProcess selectQjProcessByProcessId(String processId);

    /**
     * 查询请假流程列表
     * 
     * @param qjProcess 请假流程
     * @return 请假流程集合
     */
    public List<QjProcess> selectQjProcessList(QjProcess qjProcess);

    /**
     * 新增请假流程
     * 
     * @param qjProcess 请假流程
     * @return 结果
     */
    public int insertQjProcess(QjProcess qjProcess);

    /**
     * 修改请假流程
     * 
     * @param qjProcess 请假流程
     * @return 结果
     */
    public int updateQjProcess(QjProcess qjProcess);

    /**
     * 删除请假流程
     * 
     * @param processId 请假流程主键
     * @return 结果
     */
    public int deleteQjProcessByProcessId(String processId);

    /**
     * 批量删除请假流程
     * 
     * @param processIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteQjProcessByProcessIds(String[] processIds);
}
