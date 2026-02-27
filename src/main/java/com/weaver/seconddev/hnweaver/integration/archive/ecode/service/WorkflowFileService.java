package com.weaver.seconddev.hnweaver.integration.archive.ecode.service;

import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveDataModel;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveWorkflowConfig;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.WorkflowFileInfo;

import java.util.List;

/**
 * @author 姚礼林
 * @desc 获取流程表单文件
 * @date 2025/8/19
 **/
public interface WorkflowFileService {

    /**
     * 获取流程表单文件
     * @param dataModel 档案推送参数
     * @param config 流程档案推送配置
     * @param tempFileDir 临时文件目录
     * @return 流程表单文件
     */
    List<WorkflowFileInfo> getFiles(ArchiveDataModel dataModel , ArchiveWorkflowConfig config, String tempFileDir);

}
