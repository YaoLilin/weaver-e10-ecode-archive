package com.weaver.seconddev.hnweaver.integration.archive.ecode.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.*;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.service.ArchiveFileInfoService;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.service.PackageDataXmlCreateService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * @author 姚礼林
 * @desc 档案打包服务实现类，生成档案包
 * @date 2025/9/1
 **/
@Service
@Slf4j
public class ArchivePackServiceImpl extends AbstractArchivePackService {
    private final PackageDataXmlCreateService packageDataXmlCreateService;

    @Autowired
    public ArchivePackServiceImpl(ArchiveFileInfoService archiveFileInfoService,
                                  PackageDataXmlCreateService packageDataXmlCreateService) {
        super(archiveFileInfoService);
        this.packageDataXmlCreateService = packageDataXmlCreateService;
    }

    @Override
    protected String getPackageName(ArchiveDataModel dataModel, ArchiveWorkflowConfig workflowConfig) {
        ArchivePackageConfig packageConfig = workflowConfig.getPackageConfig();
        String packageNameTemplate = packageConfig.getPackageName();
        if (CharSequenceUtil.isBlank(packageNameTemplate)) {
            log.warn("未配置档案包名称，将使用默认档案包名称");
            return dataModel.getRequestId() + "-archivePackage";
        }

        String json = JSON.toJSONString(dataModel);
        JSONObject modelJson = JSON.parseObject(json);

        String result = buildPackageNameByTemplate(packageNameTemplate, modelJson);

        log.info("生成的档案包名称: {}" ,result);
        return result;
    }

    /**
     * 基于配置中的模板生成档案包名称，例如档案包模板为：{requestId}-{requestName}-档案包 ，其中 {requestId} 和 {requestName}
     * 会被替换为对应的值
     */
    private static @NotNull String buildPackageNameByTemplate(String packageNameTemplate, JSONObject modelJson) {
        // 解析模板中的占位符并替换数据
        String result = packageNameTemplate;

        // 使用正则表达式匹配 {fieldName} 格式的占位符
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\{([^}]+)}");
        java.util.regex.Matcher matcher = pattern.matcher(packageNameTemplate);

        while (matcher.find()) {
            String placeholder = matcher.group(0);
            String fieldName = matcher.group(1);

            // 从modelJson中获取对应的值
            String fieldValue = modelJson.getString(fieldName);
            if (fieldValue != null) {
                result = result.replace(placeholder, fieldValue);
                log.debug("替换占位符: {} -> {}", placeholder, fieldValue);
            } else {
                log.warn("未找到字段值: {}，占位符 {} 将保持原样", fieldName, placeholder);
            }
        }
        return result;
    }

    @Override
    protected File createPackageDataXml(ArchiveDataModel dataModel,List<ArchiveFileInfo> archiveFileInfoList,
                                        ArchiveWorkflowConfig workflowConfig,
                                        long requestId, String tempDir) {
        return packageDataXmlCreateService.createXml(dataModel,archiveFileInfoList, workflowConfig, tempDir);
    }

}
