package com.ruoyi.project.system.file.domain;

import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 文件记录对象 sys_file
 * 
 * @author ruoyi
 * @date 2022-01-26
 */
@Getter
@Setter
@ToString
public class File extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 文件id */
    private String fileId;

    /** 文件显示名 */
    @Excel(name = "文件名")
    private String fileName;
    
    /** 文件类型 */
    @Excel(name = "文件类型")
    private String fileType;

    /** URL地址 */
    @Excel(name = "URL地址")
    private String url;

    /** 是否是复制的，0-不是，1-是 */
    @Excel(name = "是否复制")
    private String isCopy;

    /** 若存在关联业务，则填入类型标识，如：表名 */
    @Excel(name = "关联业务")
    private String ref;

    /** 关联业务id */
    @Excel(name = "业务主键")
    private String refId;
    
    //
    private String newRef;
    private String newRefId;
    private String authUser;
    private String authFlag;
}
