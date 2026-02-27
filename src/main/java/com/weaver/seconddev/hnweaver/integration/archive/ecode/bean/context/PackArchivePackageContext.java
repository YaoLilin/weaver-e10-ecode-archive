package com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.context;

import cn.hutool.core.bean.BeanUtil;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveDataModel;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveWorkflowConfig;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.WorkflowFileInfo;
import lombok.Data;

import java.io.File;
import java.util.List;

/**
 * @author 姚礼林
 * @desc 用于打包档案的上下文
 * @date 2025/9/22
 **/
@Data
public class PackArchivePackageContext {
    private ArchiveDataModel dataModel;
    private List<WorkflowFileInfo> workflowFileInfoList;
    private File metadataXmlFile;
    private ArchiveWorkflowConfig workflowConfig;
    private String tempDir;

    public PackArchivePackageContext(ArchiveDataModel dataModel) {
        this.dataModel = dataModel;
    }

    public static PackArchivePackageContext from(ArchivePushContext context) {
        return BeanUtil.copyProperties(context, PackArchivePackageContext.class);
    }
}
