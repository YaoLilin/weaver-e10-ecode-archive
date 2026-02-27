package com.weaver.seconddev.hnweaver.integration.archive.ecode.bean;

import lombok.Data;

/**
 * @author 姚礼林
 * @desc 档案推送接口配置
 * @date 2025/9/1
 **/
@Data
public class ArchiveApiConfig {
    private String configName;
    private boolean uploadFtpEnable;
    private String ftpAddress;
    private Integer ftpPort;
    private String ftpUsername;
    private String ftpPassword;
    private String ftpFilePath;
}
