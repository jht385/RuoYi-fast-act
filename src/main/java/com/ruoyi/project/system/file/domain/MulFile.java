package com.ruoyi.project.system.file.domain;

import java.util.List;

import com.ruoyi.framework.web.domain.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class MulFile extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    
    private String ref;

    private String refId;
    
    private String filesStr;

    private List<File> files;
}
