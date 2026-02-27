package com.weaver.seconddev.hnweaver.integration.archive.ecode.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.weaver.eteams.file.client.file.FileObj;
import com.weaver.eteams.file.client.remote.FileClientService;
import com.weaver.file.ud.api.FileDownloadService;
import com.weaver.seconddev.hnweaver.common.exception.FieldNotFoundException;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.*;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.exception.WorkflowFileHandleException;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.service.FileConverter;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author 姚礼林
 * @desc 获取流程文件，包含流程正文、草稿、附件等，以及表单页面文件
 * @date 2025/8/20
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowFileServiceImpl extends AbstractWorkflowFileService{
    private final WorkflowFieldHelper workflowFieldHelper;
    private final FileDownloadService downloadService;
    private final FileConverter fileConverter;
    private final FileClientService fileClientService;

    @Override
    public List<WorkflowFileInfo> getFormFiles(ArchiveDataModel dataModel, ArchiveWorkflowConfig config,
                                               String tempFileDir) {
        List<WorkflowFileInfo> fileInfoList = new ArrayList<>();
        for (ArchiveFormFileConfig formFileConfig : config.getFormFileConfigs()) {
            log.debug("获取流程文件，文件类型：{}", formFileConfig.getFileCategoryName());
            List<WorkflowFileInfo> formFiles = getWorkflowFiles(dataModel, tempFileDir,
                    formFileConfig);
            if (formFileConfig.isFormPage()) {
                List<WorkflowFileInfo> formPageFiles = getFormPageFiles(dataModel, tempFileDir, formFileConfig);
                fileInfoList.addAll(formPageFiles);
            }
            fileInfoList.addAll(formFiles);
        }

        return fileInfoList;
    }

    @Override
    protected void convertFiles(List<WorkflowFileInfo> fileInfoList, ArchiveWorkflowConfig config) {
        for (WorkflowFileInfo fileInfo : fileInfoList) {
            String suffix = FileUtil.getSuffix(fileInfo.getFileName());
            String transFormat = fileInfo.getFormFileConfig().getTransFormat();

            if (CharSequenceUtil.isNotBlank(transFormat) && !CharSequenceUtil.equals(suffix, transFormat)) {
                File newFile = convertFile(new File(fileInfo.getFilePath()), transFormat, fileInfo.getFileId());
                fileInfo.setFilePath(newFile.getAbsolutePath());
                fileInfo.setFileName(newFile.getName());
            }
        }
    }

    private @NotNull List<WorkflowFileInfo> getFormPageFiles(ArchiveDataModel dataModel, String tempFileDir,
                                                     ArchiveFormFileConfig formFileConfig) {
        List<WorkflowFileInfo> formPageFiles = new ArrayList<>();
        ArchiveDataModel.FieldItem formPageField = getFormPageFieldParam(dataModel, formFileConfig.getTransFormat());

        if (CollUtil.isNotEmpty(formPageField.getFileList())) {
            ArchiveDataModel.FileItem fileItem = formPageField.getFileList().get(0);
            WorkflowFileInfo fileInfo = getFile(tempFileDir, fileItem, formFileConfig);
            formPageFiles.add(fileInfo);
        }else {
            throw new WorkflowFileHandleException("未能取到流程表单页面文件，参数中不含表单页面文件参数，" +
                    "请确认是否在归档方案设置中勾选导出流程表单页面");
        }
        return formPageFiles;
    }

    private @NotNull File convertFile(File file, String targetFormat, long fileId) {
        String newFileName = file.getName().substring(0, file.getName().
                lastIndexOf(".")) + "." + targetFormat;
        String newFilePath = file.getParent() + File.separator + newFileName;
        FileConvertParam param = new FileConvertParam(file.getAbsolutePath(), newFilePath, targetFormat,
                fileId);
        if (!fileConverter.convert(param)) {
            throw new WorkflowFileHandleException("文件转换失败，文件ID：" + fileId
                    + "，文件路径：" + file.getAbsolutePath());
        }
        return new File(newFilePath);
    }


    /**
     * 获取档案推送参数中的流程表单页面字段参数，后续可以用于获取流程表单页面文件
     */
    private static ArchiveDataModel.@NotNull FieldItem getFormPageFieldParam(ArchiveDataModel dataModel,
                                                                             String transFileFormat) {
        log.info("获取流程表单页面文件，配置的转换格式：{}", transFileFormat);
        final String htmlFieldName = "htmlField";
        final String pdfFieldName = "pdfField";
        // 查找pdf表单页面文件
        Optional<ArchiveDataModel.FieldItem> formPdfFieldOp = dataModel.getFieldList().stream()
                .filter(i -> pdfFieldName.equals(i.getFieldName())).findAny();
        // 查找html表单页面文件
        Optional<ArchiveDataModel.FieldItem> formHtmlFieldOp = dataModel.getFieldList().stream()
                .filter(i -> htmlFieldName.equals(i.getFieldName())).findAny();
        if (!formPdfFieldOp.isPresent() && !formHtmlFieldOp.isPresent()) {
            throw new WorkflowFileHandleException("未找到流程表单页面文件（pdf或html），" +
                    "请确认是否在归档方案设置中勾选了导出表单页面");
        }
        log.info("html表单页面是否存在：{},pdf表单页面是否存在:{}", formHtmlFieldOp.isPresent(),
                formPdfFieldOp.isPresent());

        // 如果没有配置转换格式，则取pdf文件
        if (CharSequenceUtil.isBlank(transFileFormat)) {
            log.info("没有配置表单页面转换格式，默认获取pdf页面文件");
            if (formPdfFieldOp.isPresent()) {
                return formPdfFieldOp.get();
            }
            log.warn("没有找到pdf页面文件，切换寻找html文件");
            return formHtmlFieldOp.get();
        }

        // 如果配置了转换格式，则取对应转换格式的文件
        if ("html".equalsIgnoreCase(transFileFormat)) {
            return formHtmlFieldOp.orElseGet(formPdfFieldOp::get);
        } else if ("pdf".equalsIgnoreCase(transFileFormat)) {
            return formPdfFieldOp.orElseGet(formHtmlFieldOp::get);
        }else {
            // 对于其它文件格式，优先取html文件，如果没有则取pdf文件
            return formHtmlFieldOp.orElseGet(formPdfFieldOp::get);
        }
    }

    private List<WorkflowFileInfo> getWorkflowFiles(ArchiveDataModel dataModel, String tempFileDir,
                                                    ArchiveFormFileConfig formFileConfig) {
        List<Long> formFields = formFileConfig.getFormFields();
        log.debug("表单文件字段:{}", formFields);
        List<WorkflowFileInfo> fileInfoList = new ArrayList<>();
        for (Long fieldId : formFields) {
            fileInfoList.addAll(getFilesFromModelParam(dataModel, tempFileDir, fieldId, formFileConfig));
        }
        if (formFileConfig.isRequired() && CollUtil.isEmpty(fileInfoList)) {
            throw new WorkflowFileHandleException("未取到表单文件，该文件类型为必需，文件类型:"+
                    formFileConfig.getFileCategoryName());
        }
        return fileInfoList;
    }

    private List<WorkflowFileInfo> getFilesFromModelParam(ArchiveDataModel dataModel, String tempFileDir, Long fieldId,
                                                          ArchiveFormFileConfig formFileConfig) {
        List<WorkflowFileInfo> fileInfoList = new ArrayList<>();
        Optional<ArchiveDataModel.FieldItem> fieldOp = workflowFieldHelper.getFieldParam(dataModel, fieldId);
        if (!fieldOp.isPresent()) {
            throw new FieldNotFoundException("找不到该字段，字段ID：" + fieldId);
        }
        ArchiveDataModel.FieldItem field = fieldOp.get();
        if (CollUtil.isNotEmpty(field.getFileList())) {
            for (ArchiveDataModel.FileItem fileItem : field.getFileList()) {
                WorkflowFileInfo fileInfo = getFile(tempFileDir, fileItem,formFileConfig);
                fileInfoList.add(fileInfo);
            }
        }
        return fileInfoList;
    }

    private @NotNull WorkflowFileInfo getFile(String tempFileDir,
                                              ArchiveDataModel.FileItem fileItem,ArchiveFormFileConfig formFileConfig) {
        long fileId = Long.parseLong(fileItem.getFileId());
        FileObj fileObj = fileClientService.get(fileId);

        File file = saveFile(fileItem, tempFileDir);
        WorkflowFileInfo fileInfo = new WorkflowFileInfo();
        fileInfo.setFileName(fileObj.getName());
        fileInfo.setFileId(fileId);
        fileInfo.setFilePath(file.getAbsolutePath());
        fileInfo.setFileCategoryId(formFileConfig.getFileCategoryId());
        fileInfo.setFileCategoryName(formFileConfig.getFileCategoryName());
        fileInfo.setFileCategoryMark(formFileConfig.getCategoryMark());
        fileInfo.setFormFileConfig(formFileConfig);
        return fileInfo;
    }

    private File saveFile(ArchiveDataModel.FileItem fileItem, String tempFileDir) {
        String fileId = fileItem.getFileId();
        String fileName = fileItem.getFileName();
        String savePath = tempFileDir + fileName;
        if (Files.exists(Paths.get(savePath))) {
            fileName = fileId + "-" + fileName;
            savePath = tempFileDir + fileName;
        }
        InputStream inputStream = downloadService.downloadFile(Long.valueOf(fileId)).getInputStream();
        try {
            Files.copy(inputStream, Paths.get(savePath));
        } catch (IOException e) {
            throw new WorkflowFileHandleException("获取流程文件保存到临时目录失败，文件路径：" + savePath, e);
        }
        return new File(savePath);
    }
}
