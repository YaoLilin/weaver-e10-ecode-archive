package com.weaver.seconddev.hnweaver.integration.archive.ecode.service;

import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveWorkflowConfig;

import java.io.File;

/**
 * @author 姚礼林
 * @desc 档案包上传业务类
 * @date 2025/9/1
 **/
public interface ArchivePackageUploadService {

    /**
     * 上传档案包到服务器
     * @param packageFile 档案包文件
     * @param config 档案推送配置
     * @return 是否上传成功
     */
    boolean upload(File packageFile, ArchiveWorkflowConfig config);
}
