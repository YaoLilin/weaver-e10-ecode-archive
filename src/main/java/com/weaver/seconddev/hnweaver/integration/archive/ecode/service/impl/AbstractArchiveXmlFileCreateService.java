package com.weaver.seconddev.hnweaver.integration.archive.ecode.service.impl;

import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.*;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.service.ArchiveXmlFileGenerator;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author 姚礼林
 * @desc 生成xml文件抽象类
 * @date 2025/8/21
 **/
@Slf4j
public abstract class AbstractArchiveXmlFileCreateService implements ArchiveXmlFileGenerator {

    @Override
    public File createXml(ArchiveXmlFileGenerateParam param) {
        ArchiveWorkflowConfig workflowConfig = param.getWorkflowConfig();
        ArchiveDataModel dataModel = param.getDataModel();
        List<ArchiveMetadata> metadataList = param.getArchiveMetadata();
        List<WorkflowFileInfo> workflowFileList = param.getWorkflowFileList();
        String tempDir = param.getTempDir();
        String metadataXmlTemplate = getXmlTemplate(workflowConfig);

        // 准备模板数据
        Map<String, Object> templateData = prepareTemplateData(dataModel,metadataList,workflowFileList);

        String filePath = getFilePath(tempDir, workflowConfig);
        log.debug("xml文件路径：{}", filePath);
        return createXmlFile(templateData, metadataXmlTemplate, filePath, metadataList, workflowConfig);
    }

    /**
     * 获取xml模板
     *
     * @param workflowConfig 档案推送配置
     * @return xml模板
     */
    protected abstract @NotNull String getXmlTemplate(ArchiveWorkflowConfig workflowConfig);

    /**
     * 准备xml模板数据
     *
     * @param dataModel 档案推送参数
     * @param metadataList 档案元数据列表
     * @param workflowFileList 流程文件列表
     * @return xml模板数据
     */
    protected abstract Map<String, Object> prepareTemplateData(ArchiveDataModel dataModel,
                                                               List<ArchiveMetadata> metadataList,
                                                            List<WorkflowFileInfo> workflowFileList);

    /**
     * 获取xml文件名
     * @param tempDir 临时目录
     * @param workflowConfig 档案推送配置
     * @return xml文件名
     */
    protected abstract @NotNull String getFilePath(String tempDir, ArchiveWorkflowConfig workflowConfig);


    /**
     * 创建xml文件
     *
     * @param templateData        xml模板数据
     * @param metadataXmlTemplate xml模板
     * @param xmlFilePath         xml文件路径
     * @param metadataList        元数据列表
     * @param workflowConfig      流程档案推送配置
     * @return xml文件
     */
    protected abstract File createXmlFile(Map<String, Object> templateData, String metadataXmlTemplate, String xmlFilePath,
                                       List<ArchiveMetadata> metadataList, ArchiveWorkflowConfig workflowConfig);

}
