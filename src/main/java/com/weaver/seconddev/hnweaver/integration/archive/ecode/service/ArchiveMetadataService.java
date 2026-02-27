package com.weaver.seconddev.hnweaver.integration.archive.ecode.service;

import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveDataModel;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveMetadata;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveWorkflowConfig;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.WorkflowFileInfo;

import java.util.List;

/**
 * @author 姚礼林
 * @desc 生成档案元数据业务类
 * @date 2025/8/19
 **/
public interface ArchiveMetadataService {

    /**
     * 构建元数据，这些元数据可用于创建元数据XML文件
     * @param dataModel 档案传入参数
     * @param config 档案推送配置
     * @param workflowFileInfoList 流程表单文件信息
     * @return 档案元数据
     */
    List<ArchiveMetadata> buildMetadata(ArchiveDataModel dataModel, ArchiveWorkflowConfig config,
                                        List<WorkflowFileInfo> workflowFileInfoList);
}
