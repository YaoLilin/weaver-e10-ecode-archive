package com.weaver.seconddev.hnweaver.integration.archive.ecode.bean;

import lombok.Data;

import java.io.File;
import java.util.List;

/**
 * @author 姚礼林
 * @desc 档案包内文件信息
 * @date 2025/9/1
 **/
@Data
public class PackageFile {
    private List<WorkflowFileInfo> workflowFileInfoList;
    private File metadataXmlFile;
}
