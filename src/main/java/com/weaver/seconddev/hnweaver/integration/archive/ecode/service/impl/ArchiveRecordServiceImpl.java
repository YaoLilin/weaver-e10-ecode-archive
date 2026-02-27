package com.weaver.seconddev.hnweaver.integration.archive.ecode.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.weaver.seconddev.hnweaver.common.EbFormDataChangeClient;
import com.weaver.seconddev.hnweaver.common.SqlExecuteClient;
import com.weaver.seconddev.hnweaver.common.bean.FormData;
import com.weaver.seconddev.hnweaver.common.bean.FormFieldData;
import com.weaver.seconddev.hnweaver.common.bean.ResultAndMsg;
import com.weaver.seconddev.hnweaver.common.bean.SqlExecuteResult;
import com.weaver.seconddev.hnweaver.common.constants.DatasourceGroupType;
import com.weaver.seconddev.hnweaver.common.constants.DetailUpdateType;
import com.weaver.seconddev.hnweaver.common.domain.entity.FormEntity;
import com.weaver.seconddev.hnweaver.common.exception.FormNotFoundException;
import com.weaver.seconddev.hnweaver.common.exception.SqlExecuteException;
import com.weaver.seconddev.hnweaver.common.service.FormInfoService;
import com.weaver.seconddev.hnweaver.common.util.SqlUtil;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.constants.ArchiveResultStatus;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.domaim.dto.ArchiveRecordDto;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.service.ArchiveRecordService;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.util.UserUtil;
import com.weaver.teams.domain.user.SimpleEmployee;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author 姚礼林
 * @desc 处理档案系统反馈，接收档案系统归档结果
 * @date 2025/9/8
 **/
@RequiredArgsConstructor
@Slf4j
@Service
public class ArchiveRecordServiceImpl implements ArchiveRecordService {
    @Setter
    private String tableName = "uf_archive_feedback_result";
    private final EbFormDataChangeClient ebFormDataChangeClient;
    private final SqlExecuteClient sqlExecuteClient;
    private final UserUtil userUtil;
    private final FormInfoService formInfoService;

    @Override
    public boolean updateOrInsertRecord(ArchiveRecordDto recordDto, boolean insert) {
        SimpleEmployee user = userUtil.getUser();
        Optional<FormEntity> formOp = formInfoService.getFormByTableName(DatasourceGroupType.WEAVER_EBUILDER_FORM_SERVICE,
                tableName, user.getTenantKey());
        if (!formOp.isPresent()) {
            throw new FormNotFoundException("无法查询到表信息，表名：" + tableName);
        }
        FormEntity form = formOp.get();

        List<FormFieldData> fieldData = buildFieldData(recordDto);
        List<String> conditionFields = CollUtil.toList("request_id");

        FormData formData = new FormData();
        formData.setFormId(form.getId());
        formData.setMainFieldData(fieldData);

        ResultAndMsg result = ebFormDataChangeClient.batchUpdateByConditionFields(CollUtil.toList(formData), form.getId(),
                conditionFields, insert, DetailUpdateType.UPDATE, user);
        if (!result.isSuccess()) {
            log.error("表单数据保存失败：{}", result.getMsg());
            return false;
        }
        return true;
    }

    @Override
    public boolean existRecord(String archiveId) {
        String sql = "SELECT ID FROM " + tableName + " WHERE request_id=? AND " + SqlUtil.NO_DELETE;
        SqlExecuteResult result = sqlExecuteClient.executeSql(DatasourceGroupType.WEAVER_EBUILDER_FORM_SERVICE,
                sql, archiveId);
        if (!result.isSuccess()) {
            log.error("查询sql错误，sql:{}；archiveId：{}", sql, archiveId);
            return false;
        }
        return !result.getRecords().isEmpty();
    }

    @Override
    public List<Long> getAllFailedRequestIds() {
        String sql = "SELECT REQUEST_ID FROM " + tableName + " WHERE status not in (?,?)";
        SqlExecuteResult result = sqlExecuteClient.executeSql(DatasourceGroupType.WEAVER_EBUILDER_FORM_SERVICE,
                sql, ArchiveResultStatus.SUCCESS.getValue(), ArchiveResultStatus.WAITING.getValue());
        if (!result.isSuccess()) {
            throw new SqlExecuteException("查询所有失败的档案请求id出错，sql执行出错");
        }
        return result.getRecords().stream().map(record -> Convert.toLong(record.get("REQUEST_ID")))
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    private static @NotNull List<FormFieldData> buildFieldData(ArchiveRecordDto recordDto) {
        List<FormFieldData> fieldData = new ArrayList<>();
        if (recordDto.getRequestId() != null) {
            fieldData.add(new FormFieldData("request_id", recordDto.getRequestId().toString()));
        }
        if (recordDto.getWorkflow() != null) {
            fieldData.add(new FormFieldData("workflow", recordDto.getWorkflow().toString()));
        }
        if (recordDto.getStatus() != null) {
            fieldData.add(new FormFieldData("status",String.valueOf(recordDto.getStatus().getValue())));
        }
        if (recordDto.getFeedbackMsg() != null) {
            fieldData.add(new FormFieldData("feedback_msg", recordDto.getFeedbackMsg()));
        }
        if (recordDto.getPushedTime() != null) {
            fieldData.add(new FormFieldData("pushed_time", recordDto.getPushedTime()));
        }
        if (recordDto.getMsg() != null) {
            fieldData.add(new FormFieldData("msg", recordDto.getMsg()));
        }
        if (recordDto.getArchivePackage() != null) {
            FormFieldData field = new FormFieldData("archive_package", "");
            field.setFile(true);
            field.setFilePath(recordDto.getArchivePackage().getAbsolutePath());
            fieldData.add(field);
        }
        return fieldData;
    }

}
