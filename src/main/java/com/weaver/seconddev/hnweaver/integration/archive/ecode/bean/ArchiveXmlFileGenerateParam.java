package com.weaver.seconddev.hnweaver.integration.archive.ecode.bean;

import cn.hutool.core.bean.BeanUtil;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.context.ArchivePushContext;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.context.CreateMetadataXmlFileContext;
import lombok.Data;

import java.util.List;

/**
 * @author 姚礼林
 * @desc 生成元数据xml文件参数
 * @date 2025/9/22
 **/
@Data
public class ArchiveXmlFileGenerateParam {
    /**
     * 档案推送参数
     */
    private final ArchiveDataModel dataModel;
    /**
     * 档案元数据列表
     */
    private List<ArchiveMetadata> archiveMetadata;
    /**
     * 流程表单文件信息
     */
    private List<WorkflowFileInfo> workflowFileList;
    /**
     * 流程档案配置
     */
    private ArchiveWorkflowConfig workflowConfig;
    /**
     * 临时目录
     */
    private String tempDir;

    public ArchiveXmlFileGenerateParam(ArchiveDataModel dataModel) {
        this.dataModel = dataModel;
    }

    public static ArchiveXmlFileGenerateParam from(CreateMetadataXmlFileContext context) {
        return BeanUtil.copyProperties(context, ArchiveXmlFileGenerateParam.class);
    }

}
