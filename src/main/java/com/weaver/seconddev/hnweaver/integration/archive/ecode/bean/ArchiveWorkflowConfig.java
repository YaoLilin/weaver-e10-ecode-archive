package com.weaver.seconddev.hnweaver.integration.archive.ecode.bean;

import lombok.Data;

import java.util.List;

/**
 * @author 姚礼林
 * @desc 档案推送流程配置信息
 * @date 2025/7/25
 **/
@Data
public class ArchiveWorkflowConfig {
    private Long formDataId;
    private String configName;
    private Long workflow;
    private boolean enableAfterVersion;
    private List<ArchiveMetadataConfig> metadataConfigs;
    private ArchivePackageConfig packageConfig;
    private ArchiveApiConfig apiConfig;
    private List<ArchiveFormFileConfig> formFileConfigs;
}
