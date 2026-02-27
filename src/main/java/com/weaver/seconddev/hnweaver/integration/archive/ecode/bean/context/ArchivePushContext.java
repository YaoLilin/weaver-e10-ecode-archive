package com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.context;

import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveDataModel;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveMetadata;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveWorkflowConfig;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.WorkflowFileInfo;
import lombok.Data;

import java.io.File;
import java.util.List;

/**
 * @author 姚礼林
 * @desc 档案推送上下文
 * @date 2025/9/22
 **/
@Data
public class ArchivePushContext {
    private ArchiveDataModel dataModel;
    private ArchiveWorkflowConfig workflowConfig;
    private List<WorkflowFileInfo> workflowFileInfoList;
    private File packageZip;
    private File metadataXmlFile;
    private String tempDir;
    private List<ArchiveMetadata> archiveMetadata;

    public static class Builder {
        private final ArchiveDataModel dataModel;
        private ArchiveWorkflowConfig workflowConfig;
        private List<WorkflowFileInfo> workflowFileInfoList;
        private File packageZip;
        private File metadataXmlFile;
        private String tempDir;
        private List<ArchiveMetadata> archiveMetadata;

        public Builder(ArchiveDataModel dataModel) {
            this.dataModel = dataModel;
        }

        public Builder workflowConfig(ArchiveWorkflowConfig workflowConfig) {
            this.workflowConfig = workflowConfig;
            return this;
        }

        public Builder workflowFileInfoList(List<WorkflowFileInfo> workflowFileInfoList) {
            this.workflowFileInfoList = workflowFileInfoList;
            return this;
        }

        public Builder packageZip(File packageZip) {
            this.packageZip = packageZip;
            return this;
        }

        public Builder metadataXmlFile(File metadataXmlFile) {
            this.metadataXmlFile = metadataXmlFile;
            return this;
        }

        public Builder tempDir(String tempDir) {
            this.tempDir = tempDir;
            return this;
        }

        public Builder archiveMetadata(List<ArchiveMetadata> archiveMetadata) {
            this.archiveMetadata = archiveMetadata;
            return this;
        }

        public ArchivePushContext build() {
            return new ArchivePushContext(this);
        }
    }

    public ArchivePushContext(Builder builder) {
        this.dataModel = builder.dataModel;
        this.workflowConfig = builder.workflowConfig;
        this.workflowFileInfoList = builder.workflowFileInfoList;
        this.packageZip = builder.packageZip;
        this.metadataXmlFile = builder.metadataXmlFile;
        this.tempDir = builder.tempDir;
        this.archiveMetadata = builder.archiveMetadata;
    }

    public ArchivePushContext() {
    }
}
