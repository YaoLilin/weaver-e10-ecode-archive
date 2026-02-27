package com.weaver.seconddev.hnweaver.integration.archive.ecode.config.interfaces;

import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveApiConfig;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchivePackageConfig;

import java.util.Optional;

/**
 * @author 姚礼林
 * @desc 获取档案推送接口配置
 * @date 2025/9/1
 **/
public interface ArchiveApiConfigInterface {

    /**
     * 获取档案推送接口配置
     * @param id  档案推送接口配置id
     * @return 档案推送接口配置
     */
    Optional<ArchiveApiConfig> getConfig(long id);

}
