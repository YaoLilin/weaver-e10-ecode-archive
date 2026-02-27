package com.weaver.seconddev.hnweaver.integration.archive.ecode.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.SecureUtil;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.*;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.exception.ArchiveFileHandleException;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.service.ArchiveFileInfoService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author 姚礼林
 * @desc 获取档案包内文件信息业务类，获取档案包内有哪些文件，以及位于档案包内的存放路径，但不包含数据包封套文件
 * @date 2025/8/21
 **/
@Service
@Slf4j
public class ArchiveFileInfoServiceImpl implements ArchiveFileInfoService {

    @Override
    public List<ArchiveFileInfo> getArchiveFileInfoList(List<WorkflowFileInfo> fileInfoList,
                                                        File metadataXmlFile,
                                                        ArchiveWorkflowConfig config) {
        ArchivePackageConfig packageConfig = config.getPackageConfig();
        List<FormFilePackageConfig> formFilePackageConfigs = packageConfig.getFormFilePackageConfigs();
        String basePath = getWorkflowFileBasePath(formFilePackageConfigs, packageConfig);
        log.info("文件基础路径：{}",basePath);
        List<ArchiveFileInfo> archiveFileInfoList = new ArrayList<>(getWorkflowArchiveFileList(fileInfoList,
                formFilePackageConfigs, basePath));

        if (metadataXmlFile != null) {
            ArchiveFileInfo archiveFileInfo = getMetadataXmlFileInfo(metadataXmlFile, packageConfig);
            archiveFileInfoList.add(archiveFileInfo);
        }

        return archiveFileInfoList;
    }


    private static @NotNull ArchiveFileInfo getMetadataXmlFileInfo(File metadataXmlFile,
                                                                   ArchivePackageConfig packageConfig) {
        String metadataXmlPath = packageConfig.getMetadataXmlPath();
        if (metadataXmlPath == null) {
            metadataXmlPath = "";
        }
        if (metadataXmlPath.startsWith(File.separator)) {
            metadataXmlPath = metadataXmlPath.substring(1);
        }
        String relativePath = Paths.get(metadataXmlPath, metadataXmlFile.getName()).toString();
        return buildArchiveFileInfo(metadataXmlFile, relativePath);
    }


    private static List<ArchiveFileInfo> getWorkflowArchiveFileList(List<WorkflowFileInfo> fileInfoList,
                                                                    List<FormFilePackageConfig> formFilePackageConfigs,
                                                                    String basePath) {
        if (formFilePackageConfigs == null) {
            return Collections.emptyList();
        }
        List<ArchiveFileInfo> archiveFileList  = new ArrayList<>();
        for (WorkflowFileInfo workflowFileInfo : fileInfoList) {
            String fileRelativePath = getArchiveFileRelativePath(formFilePackageConfigs, basePath, workflowFileInfo);

            ArchiveFileInfo archiveFileInfo = buildArchiveFileInfo(new File(workflowFileInfo.getFilePath()),
                    fileRelativePath);
            archiveFileList.add(archiveFileInfo);
        }
        return archiveFileList;
    }

    private static ArchiveFileInfo buildArchiveFileInfo(File file,  String fileRelatePath) {
        ArchiveFileInfo archiveFileInfo = new ArchiveFileInfo();
        String fileMd5 = SecureUtil.md5(file);
        long size;
        try {
            size = Files.size(file.toPath());
        } catch (IOException e) {
            throw new ArchiveFileHandleException("获取文件大小失败，文件路径：" + file.getPath(), e);
        }
        archiveFileInfo.setRelativeFilePath(fileRelatePath);
        archiveFileInfo.setMd5(fileMd5);
        archiveFileInfo.setFileSize(size);
        archiveFileInfo.setFile(file);
        return archiveFileInfo;
    }

    private static @NotNull String getArchiveFileRelativePath(List<FormFilePackageConfig> formFilePackageConfigs,
                                                              String basePath, WorkflowFileInfo workflowFileInfo) {
        // 查找建模打包配置中的文件打包配置明细，是否有当前文件类型的配置
        Optional<FormFilePackageConfig> formFileConfig = formFilePackageConfigs.stream()
                .filter(i -> workflowFileInfo.getFileCategoryId(). equals(i.getFileCategory()))
                .findFirst();
        String filePath = basePath;
        // 如果明细中有配置，则取配置中的文件路径
        if (formFileConfig.isPresent()) {
            FormFilePackageConfig formFilePackageConfig = formFileConfig.get();
            String configFilePath = formFilePackageConfig.getFilePath();
            if (configFilePath.startsWith(File.separator)) {
                configFilePath = configFilePath.substring(1);
            }
            filePath += configFilePath;
        }

        return Paths.get(filePath, FileUtil.getName(workflowFileInfo.getFilePath())).toString();
    }

    /**
     * 获取档案包流程表单文件的基础相对路径，后面如果有其它路径配置，可以在基础相对路径上加上其它的路径配置
     */
    private static @NotNull String getWorkflowFileBasePath(List<FormFilePackageConfig> formFilePackageConfigs,
                                                           ArchivePackageConfig packageConfig) {
        int allFile = 4;
        // 查找打包文件配置明细中的文件类型为“全部”的配置
        Optional<FormFilePackageConfig> allFileConfigOp = formFilePackageConfigs.stream()
                .filter(i -> i.getFileCategory() == allFile).findAny();
        // 获取打包配置建模主表的流程文件存放路径配置
        String basePath = packageConfig.getFormFilePath();
        log.info("打包文件主表配置基础路径：{}", basePath);
        if (CharSequenceUtil.isBlank(basePath)) {
            basePath = "";
        }
        if (basePath.startsWith(File.separator)) {
            basePath = basePath.substring(1);
        }
        if (allFileConfigOp.isPresent()) {
            log.info("明细存在全部文件类型配置");
            basePath = Paths.get(basePath, allFileConfigOp.get().getFilePath()).toString();
            log.info("加上明细的路径配置，路径为：{}", basePath);
        }
        return basePath;
    }
}
