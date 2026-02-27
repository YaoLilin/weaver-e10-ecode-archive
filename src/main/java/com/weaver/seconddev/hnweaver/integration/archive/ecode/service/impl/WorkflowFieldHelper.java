package com.weaver.seconddev.hnweaver.integration.archive.ecode.service.impl;

import com.weaver.seconddev.hnweaver.common.constants.DatasourceGroupType;
import com.weaver.seconddev.hnweaver.common.domain.entity.FormFieldEntity;
import com.weaver.seconddev.hnweaver.common.service.FieldInfoService;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveDataModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author 姚礼林
 * @desc 流程文件处理助手
 * @date 2025/8/19
 **/
@Component
@RequiredArgsConstructor
@Slf4j
final class WorkflowFieldHelper {
    private final FieldInfoService fieldInfoService;

    public Optional<ArchiveDataModel.FieldItem> getFieldParam(ArchiveDataModel dataModel, long fieldId) {
        Optional<FormFieldEntity> fieldOp = fieldInfoService
                .getFieldById(DatasourceGroupType.WEAVER_WORKFLOW_LIST_SERVICE, fieldId);
        if (!fieldOp.isPresent()) {
            log.error("无法找到对应字段，字段id：{}", fieldId);
            return Optional.empty();
        }
        FormFieldEntity field = fieldOp.get();
        return dataModel.getFieldList().stream()
                .filter(i -> i.getFieldName().equals(field.getDataKey()))
                .findAny();
    }

}
