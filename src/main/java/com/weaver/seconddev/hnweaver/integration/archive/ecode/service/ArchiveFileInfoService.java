package com.weaver.seconddev.hnweaver.integration.archive.ecode.service;

import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveFileInfo;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveWorkflowConfig;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.WorkflowFileInfo;

import java.io.File;
import java.util.List;

/**
 * @author 姚礼林
 * @desc 获取档案包内文件信息业务类，获取档案包内有哪些文件，以及位于档案包内的存放路径，但不包含数据包封套文件
 * @date 2025/8/21
 **/
public interface ArchiveFileInfoService {

    /**
     * 获取档案包内的文件信息
     * @param fileInfoList 流程文件信息
     * @param metadataXmlFile 元数据xml文件
     * @param config 流程档案配置
     * @return 档案包内的文件信息
     */
    List<ArchiveFileInfo> getArchiveFileInfoList(List<WorkflowFileInfo> fileInfoList,
                                                 File metadataXmlFile,
                                                 ArchiveWorkflowConfig config);
}
