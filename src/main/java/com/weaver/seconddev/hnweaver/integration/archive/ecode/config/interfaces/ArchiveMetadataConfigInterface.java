package com.weaver.seconddev.hnweaver.integration.archive.ecode.config.interfaces;

import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveMetadataConfig;

import java.util.List;

/**
 * @author 姚礼林
 * @desc 获取档案元数据配置
 * @date 2025/9/3
 **/
public interface ArchiveMetadataConfigInterface {

    /**
     * 获取档案元数据配置
     * @param formDataId 配置建模表单数据id，用于关联明细数据
     * @return 档案元数据配置
     */
    List<ArchiveMetadataConfig> getConfig(long formDataId);

}
