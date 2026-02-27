package com.weaver.seconddev.hnweaver.integration.archive.ecode.bean;

import lombok.Data;

import java.util.List;

/**
 * @author 姚礼林
 * @desc 档案流程表单文件配置
 * @date 2025/9/2
 **/
@Data
public class ArchiveFormFileConfig {
    private Integer fileCategoryId;
    private String fileCategoryName;
    private List<Long> formFields;
    private boolean required;
    private String transFormat;
    private String metadataName;
    private String categoryMark;
    private boolean isFormPage;
}
