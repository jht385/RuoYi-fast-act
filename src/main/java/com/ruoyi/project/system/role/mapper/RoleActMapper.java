package com.ruoyi.project.system.role.mapper;

import java.util.List;
import com.ruoyi.project.system.role.domain.RoleAct;

/**
 * 角色与工作流类型对应关系Mapper接口
 * 
 * @author ruoyi
 * @date 2022-02-12
 */
public interface RoleActMapper 
{
    /**
     * 查询角色与工作流类型对应关系
     * 
     * @param id 角色与工作流类型对应关系主键
     * @return 角色与工作流类型对应关系
     */
    public RoleAct selectRoleActById(Long id);

    /**
     * 查询角色与工作流类型对应关系列表
     * 
     * @param roleAct 角色与工作流类型对应关系
     * @return 角色与工作流类型对应关系集合
     */
    public List<RoleAct> selectRoleActList(RoleAct roleAct);

    /**
     * 新增角色与工作流类型对应关系
     * 
     * @param roleAct 角色与工作流类型对应关系
     * @return 结果
     */
    public int insertRoleAct(RoleAct roleAct);

    /**
     * 修改角色与工作流类型对应关系
     * 
     * @param roleAct 角色与工作流类型对应关系
     * @return 结果
     */
    public int updateRoleAct(RoleAct roleAct);

    /**
     * 删除角色与工作流类型对应关系
     * 
     * @param id 角色与工作流类型对应关系主键
     * @return 结果
     */
    public int deleteRoleActById(Long id);

    /**
     * 批量删除角色与工作流类型对应关系
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRoleActByIds(String[] ids);

	public int batchRoleAct(List<RoleAct> list);
	
	public int deleteRoleActByRoleId(Long roleId);

	public int deleteRoleAct(Long[] roleIds);
}
