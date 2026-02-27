package com.weaver.seconddev.hnweaver.integration.archive.ecode.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author 姚礼林
 * @desc 档案推送配置类
 * @date 2025/7/25
 **/
@Data
@Configuration
@RefreshScope
public class ArchiveProperties {
    @Value("${archive.tempFilePath:}")
    private String tempFilePath;

    @Value("${archive.deleteTempFiles:1}")
    private String deleteTempFiles;

    /**
     * 档案推送时获取的用户id，取当前租户的管理员用户id
     */
    @Value("${archive.userId:}")
    private String userId;
}
