package com.weaver.seconddev.hnweaver.integration.archive.ecode.service.impl;

import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveDataModel;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveWorkflowConfig;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.WorkflowFileInfo;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.service.WorkflowFileService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 姚礼林
 * @desc 获取流程表单文件抽象服务类
 * @date 2025/8/19
 **/
public abstract class AbstractWorkflowFileService implements WorkflowFileService {

    @Override
    public List<WorkflowFileInfo> getFiles(ArchiveDataModel dataModel, ArchiveWorkflowConfig config,
                                           String tempFileDir) {
        // 获取流程表单文件，包括正文、草稿、附件、表单页面
        List<WorkflowFileInfo> fileInfoList = new ArrayList<>(getFormFiles(dataModel, config, tempFileDir));
        convertFiles(fileInfoList, config);
        return fileInfoList;
    }

    /**
     * 获取流程表单中的文件，包括正文、草稿、附件等
     * @param dataModel 档案推送传入参数
     * @param config 建模配置
     * @param tempFileDir 临时目录
     * @return 文件列表
     */
    protected abstract List<WorkflowFileInfo> getFormFiles(ArchiveDataModel dataModel, ArchiveWorkflowConfig config,
                                                 String tempFileDir);


    /**
     * 进行文件格式转换
     * @param fileInfoList 待转换文件列表
     * @param config 建模配置
     */
    protected abstract void convertFiles(List<WorkflowFileInfo> fileInfoList, ArchiveWorkflowConfig config);


}
