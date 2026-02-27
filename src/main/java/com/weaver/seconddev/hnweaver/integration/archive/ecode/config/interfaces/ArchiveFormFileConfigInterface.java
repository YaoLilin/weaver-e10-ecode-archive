package com.weaver.seconddev.hnweaver.integration.archive.ecode.config.interfaces;

import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveFormFileConfig;
import com.weaver.teams.domain.user.SimpleEmployee;

import java.util.List;

/**
 * @author 姚礼林
 * @desc 获取流程表单文件配置，包括获取正文、附件、草稿等流程文件配置
 * @date 2025/9/2
 **/
public interface ArchiveFormFileConfigInterface {

    /**
     * 获取流程表单文件配置
     * @param formDataId 建模配置数据id，用于关联明细数据
     * @param user 用户
     * @return 流程表单文件配置
     */
    List<ArchiveFormFileConfig> getConfig(long formDataId, SimpleEmployee user);
}
