package com.weaver.seconddev.hnweaver.integration.archive.ecode.service.impl;

import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveDataModel;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveFileInfo;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveWorkflowConfig;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.service.PackageDataXmlCreateService;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author 姚礼林
 * @desc 生成档案数据包封套xml文件抽象业务类
 * @date 2025/8/21
 **/
@Slf4j
public abstract class AbstractPackageDataXmlCreateService implements PackageDataXmlCreateService {

    @Override
    public File createXml(ArchiveDataModel dataModel,List<ArchiveFileInfo> fileList,
                          ArchiveWorkflowConfig workflowConfig, String tempDir) {
        String packageXmlTemplate = getXmlTemplate(workflowConfig);
        // 准备模板数据
        Map<String, Object> templateData = prepareTemplateData(dataModel,fileList);

        String filePath = getFilePath(tempDir, workflowConfig);
        log.debug("文件路径：{}", filePath);
        return createXmlFile(templateData, packageXmlTemplate, filePath, workflowConfig);
    }

    /**
     * 获取xml模板，需要符合FreeMark模板语法
     * @param workflowConfig 档案推送配置
     * @return xml模板
     */
    protected abstract  String getXmlTemplate(ArchiveWorkflowConfig workflowConfig);

    /**
     * 创建xml文件
     * @param templateData 模板数据
     * @param packageXmlTemplate xml模板
     * @param fileName 文件名
     * @param workflowConfig 档案推送配置
     * @return xml文件
     */
    protected abstract  File createXmlFile(Map<String, Object> templateData, String packageXmlTemplate,
                                 String fileName, ArchiveWorkflowConfig workflowConfig);

    /**
     * 获取xml文件名
     * @param tempDir 临时目录
     * @param workflowConfig 档案推送配置
     * @return xml文件名
     */
    protected abstract  String getFilePath(String tempDir, ArchiveWorkflowConfig workflowConfig);

    /**
     * 准备模板数据，用于作为xml模板内的输入数据
     * @param dataModel 档案推送参数
     * @param fileList 档案包文件列表
     * @return 模板数据
     */
    protected abstract  Map<String, Object> prepareTemplateData(ArchiveDataModel dataModel,
                                                                List<ArchiveFileInfo> fileList);
}
