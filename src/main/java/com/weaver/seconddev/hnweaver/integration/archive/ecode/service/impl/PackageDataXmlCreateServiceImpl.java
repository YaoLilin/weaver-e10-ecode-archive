package com.weaver.seconddev.hnweaver.integration.archive.ecode.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveDataModel;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveFileInfo;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchivePackageConfig;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveWorkflowConfig;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.exception.ArchiveConfigException;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.service.XmlGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 姚礼林
 * @desc  生成档案数据包封套xml文件
 * @date 2025/8/21
 **/
@Slf4j
@RequiredArgsConstructor
@Service
public class PackageDataXmlCreateServiceImpl extends AbstractPackageDataXmlCreateService {
    private final XmlGenerator xmlGenerator;

    @Override
    protected String getXmlTemplate(ArchiveWorkflowConfig workflowConfig) {
        ArchivePackageConfig packageConfig = workflowConfig.getPackageConfig();
        String packageXmlTemplate = packageConfig.getPackageXmlTemplate();
        if (CharSequenceUtil.isBlank(packageXmlTemplate)) {
            throw new ArchiveConfigException("数据包封套xml模板为空，请检查建模配置");
        }
        return packageXmlTemplate;
    }

    @Override
    protected File createXmlFile(Map<String, Object> templateData, String packageXmlTemplate, String fileName,
                              ArchiveWorkflowConfig workflowConfig) {
        return xmlGenerator.createXml(templateData, packageXmlTemplate, fileName);
    }

    @Override
    protected String getFilePath(String tempDir, ArchiveWorkflowConfig workflowConfig) {
        String packageXmlName = workflowConfig.getPackageConfig().getPackageXmlName();
        if (CharSequenceUtil.isBlank(packageXmlName)) {
            throw new ArchiveConfigException("数据包封套xml文件名称未配置，请检查档案推送配置");
        }
        log.debug("配置的xml文件名称：{}",packageXmlName);
        String fileName;
        if (packageXmlName.contains(".xml")) {
            fileName = tempDir + packageXmlName;
        }else {
            fileName = tempDir + packageXmlName + ".xml";
        }
        return fileName;
    }

    @Override
    protected Map<String, Object> prepareTemplateData(ArchiveDataModel dataModel, List<ArchiveFileInfo> fileList) {
        Map<String, Object> data = new HashMap<>(4);
        data.put("files", fileList);
        data.put("param", dataModel);
        return data;
    }

}
