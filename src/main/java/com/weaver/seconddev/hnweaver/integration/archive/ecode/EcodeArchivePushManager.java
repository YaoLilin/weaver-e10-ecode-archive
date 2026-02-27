package com.weaver.seconddev.hnweaver.integration.archive.ecode;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.*;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.context.*;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.config.ArchiveProperties;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.config.interfaces.ArchiveWorkflowConfigInterface;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.constants.ArchiveResultStatus;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.domaim.dto.ArchiveRecordDto;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.service.*;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.service.impl.FileNameHandler;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author 姚礼林
 * @desc 执行档案推送，根据推送配置，获取流程表单文件，生成元数据xml文件，打包成档案包，上传档案包到服务器
 * @date 2025/9/1
 **/
@Component
@Slf4j
public class EcodeArchivePushManager extends AbstractEcodeArchivePushManage {

    public static final String TEMP_FILE_DIR = FileUtil.getTmpDirPath() + File.separator + "archive" + File.separator;
    private final ArchiveWorkflowConfigInterface configManager;
    private final ArchiveMetadataService metadataService;
    private final WorkflowFileService workflowFileService;
    private final ArchiveXmlFileGenerator archiveXmlFileGenerator;
    private final FileNameHandler fileNameHandler;
    private final ArchivePackService packService;
    private final ArchivePackageUploadService uploadService;
    private final ArchiveRecordService recordService;

    @Autowired
    public EcodeArchivePushManager(ArchiveWorkflowConfigInterface configManager, ArchiveMetadataService metadataService,
                                   WorkflowFileService workflowFileService, ArchiveXmlFileGenerator archiveXmlFileGenerator,
                                   FileNameHandler fileNameHandler, ArchivePackService packService,
                                   ArchivePackageUploadService uploadService, ArchiveProperties archiveProperties,
                                   ArchiveRecordService recordService) {
        super(archiveProperties);
        this.configManager = configManager;
        this.metadataService = metadataService;
        this.workflowFileService = workflowFileService;
        this.archiveXmlFileGenerator = archiveXmlFileGenerator;
        this.fileNameHandler = fileNameHandler;
        this.packService = packService;
        this.uploadService = uploadService;
        this.recordService = recordService;
    }

    @Override
    @NotNull
    protected String getTempPath(TempPathContext context) {
        ArchiveDataModel dataModel = context.getDataModel();
        String tempFilePath = getArchiveProperties().getTempFilePath();
        if (CharSequenceUtil.isNotBlank(tempFilePath)) {
            return tempFilePath + File.separator + dataModel.getRequestId();
        }

        return TEMP_FILE_DIR + dataModel.getRequestId() + File.separator;
    }

    @Override
    protected @Nullable ArchiveWorkflowConfig getWorkflowConfig(GetWorkflowConfigContext context) {
        ArchiveDataModel dataModel = context.getDataModel();
        Optional<ArchiveWorkflowConfig> configOp = configManager.getConfig(Long.parseLong(dataModel.getWorkflowId()));
        return configOp.orElse(null);
    }

    @Override
    protected List<ArchiveMetadata> getArchiveMetadata(GetArchiveMetadataContext context) {
        ArchiveDataModel dataModel = context.getDataModel();
        List<ArchiveMetadata> archiveMetadata = metadataService.buildMetadata(dataModel,
                context.getWorkflowConfig(), context.getWorkflowFileInfoList());
        log.info("归档元数据：{}", archiveMetadata);
        return archiveMetadata;
    }

    @Override
    protected List<WorkflowFileInfo> getWorkflowFiles(GetWorkflowFilesContext context) {
        ArchiveWorkflowConfig workflowConfig = context.getWorkflowConfig();
        List<WorkflowFileInfo> fileInfoList = workflowFileService.getFiles(context.getDataModel(), workflowConfig,
                context.getTempDir());
        changeFormFilesName(workflowConfig, fileInfoList);
        log.info("获取到的流程文件:{}", JSON.toJSONString(fileInfoList));
        return fileInfoList;
    }

    @Override
    protected File createMetadataXmlFile(CreateMetadataXmlFileContext context) {
        return archiveXmlFileGenerator.createXml(ArchiveXmlFileGenerateParam.from(context));
    }

    @Override
    protected File packArchivePackage(PackArchivePackageContext context) {
        PackageFile packageFile = new PackageFile();
        packageFile.setWorkflowFileInfoList(context.getWorkflowFileInfoList());
        packageFile.setMetadataXmlFile(context.getMetadataXmlFile());

        return packService.pack(ArchivePackParam.create(context, packageFile));
    }

    @Override
    protected boolean uploadPackage(File packageZip, ArchivePushContext context) {
        return uploadService.upload(packageZip, context.getWorkflowConfig());
    }


    @Override
    protected boolean callApi(ArchivePushContext context) {
        return true;
    }

    @Override
    protected void whenPushFinished(ArchivePushContext context) {
        ArchiveDataModel dataModel = context.getDataModel();
        // 插入推送结果到建模表
        ArchiveRecordDto recordDto = new ArchiveRecordDto();
        recordDto.setRequestId(Long.valueOf(dataModel.getRequestId()));
        recordDto.setWorkflow(Long.valueOf(dataModel.getRequestId()));
        recordDto.setPushedTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        recordDto.setStatus(ArchiveResultStatus.WAITING);
        recordDto.setArchivePackage(context.getPackageZip());
        recordDto.setFeedbackMsg("");
        recordDto.setMsg("已推送到档案系统");

        if (recordService.updateOrInsertRecord(recordDto, true)) {
            log.info("已插入到记录建模,请求id：{}", dataModel.getRequestId());
        } else {
            log.error("插入到记录建模失败，请求id：{}", dataModel.getRequestId());
        }
    }


    private void changeFormFilesName(ArchiveWorkflowConfig workflowConfig, List<WorkflowFileInfo> fileInfoList) {
        List<FormFilePackageConfig> formFilePackageConfigs = workflowConfig.getPackageConfig()
                .getFormFilePackageConfigs();
        for (WorkflowFileInfo workflowFileInfo : fileInfoList) {
            Optional<FormFilePackageConfig> formFileConfig = formFilePackageConfigs.stream()
                    .filter(i -> workflowFileInfo.getFileCategoryId().equals(i.getFileCategory()))
                    .findFirst();
            if (formFileConfig.isPresent()) {
                String newFileName = fileNameHandler.handle(workflowFileInfo.getFileName(), formFileConfig.get());
                File file = new File(workflowFileInfo.getFilePath());
                if (!newFileName.equals(file.getName())) {
                    FileUtil.rename(file, newFileName, true);
                }
            }
        }
    }
}
