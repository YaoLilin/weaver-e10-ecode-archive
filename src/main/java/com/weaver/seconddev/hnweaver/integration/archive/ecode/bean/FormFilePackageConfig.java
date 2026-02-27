package com.weaver.seconddev.hnweaver.integration.archive.ecode.bean;

import lombok.Data;

/**
 * @author 姚礼林
 * @desc 表单文件打包配置
 * @date 2025/8/21
 **/
@Data
public class FormFilePackageConfig {
    private Integer fileCategory;
    private String filePath;
    private String filePrefix;
    private String fileSuffix;
    private boolean uuidEnable;
}
