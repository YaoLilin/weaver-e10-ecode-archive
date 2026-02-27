package com.weaver.seconddev.hnweaver.integration.archive.ecode.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ZipUtil;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.*;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.exception.ArchivePackException;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.service.ArchiveFileInfoService;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.service.ArchivePackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author 姚礼林
 * @desc 档案打包抽象业务类，将表单文件、元数据xml文件等文件进行打包，文件需要根据配置放到档案包指定位置，生成档案包
 * @date 2025/9/3
 **/
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractArchivePackService implements ArchivePackService {
    private final ArchiveFileInfoService archiveFileInfoService;

    @Override
    public File pack(ArchivePackParam param) {
        PackageFile packageFile = param.getPackageFile();
        String requestId = param.getDataModel().getRequestId();
        String tempDir = param.getTempDir();
        ArchiveWorkflowConfig workflowConfig = param.getWorkflowConfig();
        ArchiveDataModel dataModel = param.getDataModel();
        List<ArchiveFileInfo> archiveFileInfoList =
                archiveFileInfoService.getArchiveFileInfoList(packageFile.getWorkflowFileInfoList(),
                        packageFile.getMetadataXmlFile(), workflowConfig);
        File packageDataXml = createPackageDataXml(dataModel,archiveFileInfoList, workflowConfig,
                Long.parseLong(requestId), tempDir);

        String packageName = getPackageName(dataModel, workflowConfig);
        Path packagePath = Paths.get(tempDir, packageName);
        log.info("档案包路径：{}", packagePath);
        try {
            if (!Files.exists(packagePath)) {
                Files.createDirectory(packagePath);
            }
        } catch (IOException e) {
            throw new ArchivePackException("创建档案包目录失败", e);
        }

        moveFilesToPackage(archiveFileInfoList, packagePath);

        if (packageDataXml != null) {
            movePackageDataXmlToPackage(workflowConfig, packagePath, packageDataXml);
        }else {
            log.info("数据包封套文件不存在");
        }

        return ZipUtil.zip(packagePath.toFile());
    }

    /**
     * 获取档案包名称
     * @param dataModel 档案推送参数
     * @param workflowConfig 流程档案推送配置
     * @return 档案包名称
     */
    protected abstract String  getPackageName(ArchiveDataModel dataModel, ArchiveWorkflowConfig workflowConfig);

    /**
     * 创建数据包封套文件
     * @param dataModel 档案推送参数
     * @param archiveFileInfoList 档案包内的文件信息
     * @param workflowConfig 档案推送配置
     * @param requestId 请求ID
     * @param tempDir 临时目录
     * @return 数据包封套文件
     */
    protected abstract @Nullable File createPackageDataXml(ArchiveDataModel dataModel,
                                                           List<ArchiveFileInfo> archiveFileInfoList,
                                                           ArchiveWorkflowConfig workflowConfig,
                                                           long requestId, String tempDir);
    private static void moveFilesToPackage(List<ArchiveFileInfo> archiveFileInfoList, Path packagePath) {
        log.info("移动文件到档案包内");
        for (ArchiveFileInfo archiveFileInfo : archiveFileInfoList) {
            File file = archiveFileInfo.getFile();
            Path moveTargetPath = Paths.get(packagePath.toString(), archiveFileInfo.getRelativeFilePath());
            log.info("文件位于档案包内的存放路径：{}", moveTargetPath);
            try {
                // 确保目标文件的父目录存在
                Path parentDir = moveTargetPath.getParent();
                log.info("文件位于档案包内的目录：{}", parentDir);
                if (parentDir != null && Files.notExists(parentDir)) {
                    Files.createDirectories(parentDir);
                }
            } catch (IOException e) {
                throw new ArchivePackException("创建档案包内目录失败", e);
            }
            try {
                log.info("移动路径：{}", moveTargetPath);
                Files.move(file.toPath(), moveTargetPath);
            } catch (IOException e) {
                throw new ArchivePackException("移动文件到档案包失败", e);
            }
        }
    }

    private static void movePackageDataXmlToPackage(ArchiveWorkflowConfig workflowConfig,
                                                    Path packagePath, File packageDataXml) {
        Path packageXmlPath = getPackageXmlPath(workflowConfig, packagePath, packageDataXml);
        log.info("数据包封套文件存放路径：{}",packageXmlPath);
        try {
            if (Files.notExists(packageXmlPath.getParent())) {
                Files.createDirectories(packageXmlPath.getParent());
            }
            Files.move(packageDataXml.toPath(), packageXmlPath);
        } catch (IOException e) {
            throw new ArchivePackException("移动数据包封套文件到档案包失败", e);
        }
    }

    /**
     * 根据配置获取数据包封套文件存放路径
     */
    private static @NotNull Path getPackageXmlPath(ArchiveWorkflowConfig workflowConfig, Path packagePath,
                                                   File packageDataXml) {
        ArchivePackageConfig packageConfig = workflowConfig.getPackageConfig();
        String packageXmlConfigPath = packageConfig.getPackageXmlPath();
        log.info("配置中的数据包封套文件存放路径：{}", packageXmlConfigPath);
        String packageXmlRelatePath;
        if (CharSequenceUtil.isBlank(packageXmlConfigPath)) {
            packageXmlRelatePath = packagePath.toString();
        } else {
            packageXmlRelatePath = Paths.get(packagePath.toString(), packageXmlConfigPath).toString();
        }
        Path saveDir = packagePath.resolve(packageXmlRelatePath);
        return saveDir.resolve(packageDataXml.getName());
    }

}
