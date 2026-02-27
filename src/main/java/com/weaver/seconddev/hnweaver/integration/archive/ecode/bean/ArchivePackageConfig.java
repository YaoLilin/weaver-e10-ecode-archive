package com.weaver.seconddev.hnweaver.integration.archive.ecode.bean;

import lombok.Data;

import java.util.List;

/**
 * @author 姚礼林
 * @desc 档案打包配置
 * @date 2025/8/21
 **/
@Data
public class ArchivePackageConfig {
    private String packageName;
    private String configName;
    private String metadataXmlName;
    private String metadataXmlPath;
    private String packageXmlName;
    private String packageXmlPath;
    private String formFilePath;
    private String metadataXmlTemplate;
    private String packageXmlTemplate;
    private List<FormFilePackageConfig> formFilePackageConfigs;
}
