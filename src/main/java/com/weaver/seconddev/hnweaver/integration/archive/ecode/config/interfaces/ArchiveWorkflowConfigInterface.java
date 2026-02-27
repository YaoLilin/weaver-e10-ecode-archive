package com.weaver.seconddev.hnweaver.integration.archive.ecode.config.interfaces;

import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveWorkflowConfig;

import java.util.Optional;

/**
 * @author 姚礼林
 * @desc 获取建模配置的档案推送配置 接口
 * @date 2025/8/16
 **/
public interface ArchiveWorkflowConfigInterface {

    /**
     * 获取档案推送配置
     * @param workflowId 流程id
     * @return 档案推送配置
     */
    Optional<ArchiveWorkflowConfig> getConfig(long workflowId);
}
