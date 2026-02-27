package com.weaver.seconddev.hnweaver.integration.archive.ecode.service;

import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveDataModel;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveFileInfo;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveWorkflowConfig;

import java.io.File;
import java.util.List;

/**
 * @author 姚礼林
 * @desc 生成档案数据包封套xml文件
 * @date 2025/8/21
 **/
public interface PackageDataXmlCreateService {

    /**
     * 生成档案数据包封套xml文件
     * @param dataModel 档案推送参数
     * @param fileList 档案包内文件信息
     * @param workflowConfig 档案推送配置
     * @param tempDir 临时目录
     * @return xml文件
     */
    File createXml(ArchiveDataModel dataModel, List<ArchiveFileInfo> fileList,
                   ArchiveWorkflowConfig workflowConfig, String tempDir);
}
