package com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.context;

import cn.hutool.core.bean.BeanUtil;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveDataModel;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveMetadata;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveWorkflowConfig;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.WorkflowFileInfo;
import lombok.Data;

import java.util.List;

/**
 * @author 姚礼林
 * @desc 用于创建元数据xml文件的上下文
 * @date 2025/9/22
 **/
@Data
public class CreateMetadataXmlFileContext {
    private ArchiveDataModel dataModel;
    private List<WorkflowFileInfo> fileInfoList;
    private ArchiveWorkflowConfig workflowConfig;
    private List<ArchiveMetadata> archiveMetadata;
    private String tempDir;

    public CreateMetadataXmlFileContext(ArchiveDataModel dataModel) {
        this.dataModel = dataModel;
    }

    public static CreateMetadataXmlFileContext from(ArchivePushContext context) {
        return BeanUtil.copyProperties(context, CreateMetadataXmlFileContext.class);
    }
}
