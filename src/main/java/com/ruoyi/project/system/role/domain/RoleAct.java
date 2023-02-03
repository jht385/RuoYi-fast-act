package com.ruoyi.project.system.role.domain;

import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.ruoyi.framework.web.domain.BaseEntity;

/**
 * 角色与工作流类型对应关系对象 sys_role_act
 * 
 * @author ruoyi
 * @date 2022-02-12
 */
@Getter
@Setter
@ToString
public class RoleAct extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /**  */
    private Long id;

    /** 角色ID */
    @Excel(name = "角色ID")
    private Long roleId;

    /** 工作流类型 */
    @Excel(name = "工作流类型")
    private String actValue;
    
    /** 工作流类型显示名 */
    private String actName;
    /** 角色是否存在此工作流标识 默认不存在 */
    private boolean flag = false;

}
