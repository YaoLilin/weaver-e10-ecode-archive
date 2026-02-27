package com.weaver.seconddev.hnweaver.integration.archive.ecode.bean;

import lombok.Data;

/**
 * @author 姚礼林
 * @desc 档案元数据配置
 * @date 2025/7/25
 **/
@Data
public class ArchiveMetadataConfig {
    private String metadataShowName;
    private String metadataName;
    private Long formField;
    private boolean required;
    private String fixValue;
}
