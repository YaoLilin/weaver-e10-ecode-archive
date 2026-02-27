package com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.context;

import cn.hutool.core.bean.BeanUtil;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveDataModel;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveWorkflowConfig;
import lombok.Data;

/**
 * @author 姚礼林
 * @desc 用于档案获取流程文件的上下文
 * @date 2025/9/22
 **/
@Data
public class GetWorkflowFilesContext {
    private ArchiveDataModel dataModel;
    private ArchiveWorkflowConfig workflowConfig;
    private String tempDir;

    public GetWorkflowFilesContext(ArchiveDataModel dataModel) {
        this.dataModel = dataModel;
    }

    public GetWorkflowFilesContext(ArchiveDataModel dataModel, ArchiveWorkflowConfig workflowConfig, String tempDir) {
        this.dataModel = dataModel;
        this.workflowConfig = workflowConfig;
        this.tempDir = tempDir;
    }

    public static GetWorkflowFilesContext from(ArchivePushContext context) {
        return BeanUtil.copyProperties(context, GetWorkflowFilesContext.class);
    }
}
