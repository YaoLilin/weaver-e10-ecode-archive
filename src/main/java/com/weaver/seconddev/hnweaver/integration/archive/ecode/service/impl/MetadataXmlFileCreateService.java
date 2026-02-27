package com.weaver.seconddev.hnweaver.integration.archive.ecode.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveDataModel;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveMetadata;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveWorkflowConfig;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.WorkflowFileInfo;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.exception.ArchiveConfigException;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.exception.CreateXmlException;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.service.XmlGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 姚礼林
 * @desc 生成元数据xml文件
 * @date 2025/8/20
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class MetadataXmlFileCreateService extends AbstractArchiveXmlFileCreateService {
    private final XmlGenerator xmlGenerator;

    @Override
    protected  @NotNull String getXmlTemplate(ArchiveWorkflowConfig workflowConfig) {
        String metadataXmlTemplate = workflowConfig.getPackageConfig().getMetadataXmlTemplate();
        if (CharSequenceUtil.isBlank(metadataXmlTemplate)) {
            throw new CreateXmlException("元数据XML模板不能为空");
        }
        return metadataXmlTemplate;
    }

    @Override
    protected  @NotNull String getFilePath(String tempDir, ArchiveWorkflowConfig workflowConfig) {
        String metadataXmlFileName = workflowConfig.getPackageConfig().getMetadataXmlName();
        log.debug("建模中配置的元数据xml文件名称：{}", metadataXmlFileName);
        if (CharSequenceUtil.isBlank(metadataXmlFileName)) {
            throw new ArchiveConfigException("元数据xml文件名称未配置");
        }
        if (metadataXmlFileName.contains(".xml")) {
            return Paths.get(tempDir, metadataXmlFileName).toString();
        }
        return Paths.get(tempDir, metadataXmlFileName + ".xml").toString();
    }

    /**
     * 准备模板数据
     */
    @Override
    protected Map<String, Object> prepareTemplateData(ArchiveDataModel dataModel, List<ArchiveMetadata> metadataList,
                                                      List<WorkflowFileInfo> workflowFileList) {
        Map<String, Object> data = new HashMap<>(10);

        // 添加元数据列表
        data.put("metadataList", metadataList);
        data.put("param", dataModel);

        return data;
    }

    @Override
    protected File createXmlFile(Map<String, Object> templateData, String metadataXmlTemplate, String xmlFilePath,
                              List<ArchiveMetadata> metadataList, ArchiveWorkflowConfig workflowConfig) {
        return xmlGenerator.createXml(templateData, metadataXmlTemplate, xmlFilePath);
    }


}
