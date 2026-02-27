package com.weaver.seconddev.hnweaver.integration.archive.ecode.config.interfaces;

import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchivePackageConfig;

import java.util.Optional;

/**
 * @author 姚礼林
 * @desc 获取档案打包配置
 * @date 2025/8/21
 **/
public interface ArchivePackageConfigInterface {

    /**
     * 获取档案打包配置
     * @param id 打包配置id
     * @return 档案打包配置
     */
    Optional<ArchivePackageConfig> getConfig(long id);
}
