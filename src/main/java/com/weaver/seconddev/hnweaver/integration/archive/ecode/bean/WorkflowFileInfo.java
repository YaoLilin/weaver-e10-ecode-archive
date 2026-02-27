package com.weaver.seconddev.hnweaver.integration.archive.ecode.bean;

import lombok.Data;

/**
 * @author 姚礼林
 * @desc 流程表单文件信息
 * @date 2025/8/19
 **/
@Data
public class WorkflowFileInfo {
    private String fileName;
    private String filePath;
    private Long fileId;
    /**
     * 文件分类名称，中文名，为eb档案配置中的文件类型下拉框选项名称
     */
    private String fileCategoryName;
    /**
     * 文件分类标识，用于区分不同文件分类
     */
    private String fileCategoryMark;
    /**
     * 文件分类id，为eb档案配置中的文件类型下拉框选项id
     */
    private Integer fileCategoryId;
    private ArchiveFormFileConfig formFileConfig;
}
