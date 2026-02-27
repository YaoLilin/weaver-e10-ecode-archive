package com.weaver.seconddev.hnweaver.integration.archive.ecode.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.weaver.seconddev.hnweaver.common.constants.RpcGroup;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.*;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.exception.ArchiveMetadataException;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.service.ArchiveMetadataService;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.util.UserUtil;
import com.weaver.teams.domain.user.SimpleEmployee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author 姚礼林
 * @desc 生成档案元数据业务类
 * @date 2025/8/19
 **/
@Slf4j
@RequiredArgsConstructor
@Service
public class ArchiveMetadataServiceImpl implements ArchiveMetadataService {
    private final WorkflowFieldHelper workflowFieldHelper;
    private final UserUtil userUtil;


    @Override
    public List<ArchiveMetadata> buildMetadata(ArchiveDataModel dataModel,ArchiveWorkflowConfig config,
                                               List<WorkflowFileInfo> workflowFileInfoList) {
        List<ArchiveMetadataConfig> metadataConfigs = config.getMetadataConfigs();
        if (CollUtil.isEmpty(metadataConfigs)) {
            throw new ArchiveMetadataException("建模中未配置元数据，流程id：" + dataModel.getWorkflowId() + ",配置名称：" +
                    config.getConfigName());
        }

        SimpleEmployee employee = userUtil.getUser();
        List<ArchiveMetadata> metadataList = new ArrayList<>();
        for (ArchiveMetadataConfig metadataConfig : metadataConfigs) {
            log.info("元数据名称：{}" , metadataConfig.getMetadataName());
            ArchiveMetadata metadata = getArchiveMetadata(dataModel, config, metadataConfig,employee);
            metadataList.add(metadata);
        }

        // 获取文件元数据
        for (WorkflowFileInfo workflowFileInfo : workflowFileInfoList) {
            ArchiveMetadata metadata = getFileMetadata( workflowFileInfo);
            metadataList.add(metadata);
        }

        return metadataList;
    }

    private @NotNull ArchiveMetadata getFileMetadata( WorkflowFileInfo workflowFileInfo) {
        ArchiveMetadata metadata = new ArchiveMetadata();

        MetadataFileInfo fileInfo = new MetadataFileInfo();
        fileInfo.setFileName(workflowFileInfo.getFileName());
        fileInfo.setFileType(FileUtil.getSuffix(workflowFileInfo.getFilePath()));
        fileInfo.setFileCategoryName(workflowFileInfo.getFileCategoryName());
        fileInfo.setFileCategoryMark(workflowFileInfo.getFileCategoryMark());

        metadata.setName(workflowFileInfo.getFormFileConfig().getMetadataName());
        metadata.setFile(true);
        metadata.setFileInfo(fileInfo);
        return metadata;
    }

    protected @NotNull ArchiveMetadata getArchiveMetadata(ArchiveDataModel dataModel, ArchiveWorkflowConfig config,
                                                          ArchiveMetadataConfig metadataConfig, SimpleEmployee employee) {
        String value = null;
        if (CharSequenceUtil.isNotEmpty(metadataConfig.getFixValue())) {
            log.info("元数据值固定，取固定值：{}", metadataConfig.getFixValue());
            value = metadataConfig.getFixValue();
        } else if (metadataConfig.getFormField() != null) {
            value = getField(dataModel, metadataConfig,employee).getFieldValue();
        }

        if (CharSequenceUtil.isEmpty(value) && metadataConfig.isRequired()) {
            throw new ArchiveMetadataException("元数据值不允许为空，元数据名称：" + metadataConfig.getMetadataName());
        }

        ArchiveMetadata metadata = new ArchiveMetadata();
        metadata.setName(metadataConfig.getMetadataName());
        metadata.setValue(value);
        return metadata;
    }

    private ArchiveDataModel.@NotNull FieldItem getField(ArchiveDataModel dataModel,
                                                         ArchiveMetadataConfig metadataConfig, SimpleEmployee employee) {
        Optional<ArchiveDataModel.FieldItem> fieldOp =
                workflowFieldHelper.getFieldParam(dataModel, metadataConfig.getFormField());
        if (!fieldOp.isPresent()) {
            throw new ArchiveMetadataException("获取流程字段值失败，参数中无该字段，元数据名称："
                    + metadataConfig.getMetadataName());
        }
        return fieldOp.get();
    }

}
