package com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.context;

import cn.hutool.core.bean.BeanUtil;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveDataModel;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveWorkflowConfig;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.WorkflowFileInfo;
import lombok.Data;

import java.util.List;

/**
 * @author 姚礼林
 * @desc 用于生成档案元数据的上下文参数
 * @date 2025/9/22
 **/
@Data
public class GetArchiveMetadataContext {
    private ArchiveDataModel dataModel;
    private ArchiveWorkflowConfig workflowConfig;
    private List<WorkflowFileInfo> workflowFileInfoList;


    public GetArchiveMetadataContext(ArchiveDataModel dataModel) {
        this.dataModel = dataModel;
    }

    public GetArchiveMetadataContext(ArchiveDataModel dataModel, ArchiveWorkflowConfig workflowConfig,
                                     List<WorkflowFileInfo> workflowFileInfoList) {
        this.dataModel = dataModel;
        this.workflowConfig = workflowConfig;
        this.workflowFileInfoList = workflowFileInfoList;
    }

    public static GetArchiveMetadataContext from(ArchivePushContext context) {
        return BeanUtil.copyProperties(context, GetArchiveMetadataContext.class);
    }
}
