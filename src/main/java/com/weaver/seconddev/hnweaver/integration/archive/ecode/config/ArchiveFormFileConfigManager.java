package com.weaver.seconddev.hnweaver.integration.archive.ecode.config;

import cn.hutool.core.convert.Convert;
import com.weaver.seconddev.hnweaver.common.SqlExecuteClient;
import com.weaver.seconddev.hnweaver.common.bean.SqlExecuteResult;
import com.weaver.seconddev.hnweaver.common.constants.DatasourceGroupType;
import com.weaver.seconddev.hnweaver.common.domain.entity.FormFieldEntity;
import com.weaver.seconddev.hnweaver.common.exception.FieldNotFoundException;
import com.weaver.seconddev.hnweaver.common.exception.FormNotFoundException;
import com.weaver.seconddev.hnweaver.common.exception.SqlExecuteException;
import com.weaver.seconddev.hnweaver.common.service.FieldInfoService;
import com.weaver.seconddev.hnweaver.common.service.FormInfoService;
import com.weaver.seconddev.hnweaver.common.util.SqlUtil;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveFormFileConfig;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.config.interfaces.ArchiveFormFileConfigInterface;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.exception.ArchiveConfigException;
import com.weaver.teams.domain.user.SimpleEmployee;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author 姚礼林
 * @desc 获取流程表单文件配置，包括获取正文、附件、草稿等流程文件配置
 * @date 2025/9/2
 **/
@Slf4j
@Setter
@RequiredArgsConstructor
@Component
public class ArchiveFormFileConfigManager implements ArchiveFormFileConfigInterface {
    @Getter
    @Setter
    private String tableName = "uf_archive_form_file_config";
    private final SqlExecuteClient sqlExecuteClient;
    private final FormInfoService formInfoService;
    private final FieldInfoService fieldInfoService;

    @Override
    public List<ArchiveFormFileConfig> getConfig(long formDataId,SimpleEmployee user) {
        Optional<List<Map<String, Object>>> configDataOp = getConfigData(formDataId);
        if (!configDataOp.isPresent()) {
            return Collections.emptyList();
        }
        return buildFormFileConfigList(configDataOp.get(),user);
    }

    private Optional<List<Map<String, Object>>> getConfigData(long id) {
        String sql = "SELECT file_category,form_fields,required,trans_format,metadata_name,category_mark,is_form_page " +
                "FROM " + tableName + " WHERE form_data_id=? AND " + SqlUtil.NO_DELETE;
        SqlExecuteResult result = sqlExecuteClient.executeSql(DatasourceGroupType.WEAVER_EBUILDER_APP_SERVICE, sql, id);
        if (!result.isSuccess()) {
            throw new SqlExecuteException("查询表单文件配置失败，sql执行异常，sql:" + sql);
        }
        if (result.getRecords().isEmpty()) {
            log.warn("未查询到数据");
            return Optional.empty();
        }
        return Optional.of(result.getRecords());
    }

    private List<ArchiveFormFileConfig> buildFormFileConfigList(List<Map<String, Object>> configData,
                                                                  SimpleEmployee user) {
        Optional<Long> formIdOp = formInfoService.getFormIdBySubFormName(DatasourceGroupType.WEAVER_EBUILDER_FORM_SERVICE,
                tableName, user.getTenantKey());
        if (!formIdOp.isPresent()) {
            throw new FormNotFoundException("无法查询到主表id，明细表名：" + tableName);
        }
        long formId = formIdOp.get();
        log.debug("formId:{}", formId);

        List<ArchiveFormFileConfig> result = new ArrayList<>();
        for (Map<String, Object> item : configData) {
            ArchiveFormFileConfig config = new ArchiveFormFileConfig();
            String categoryName = getFieldOptionName(formId,"file_category",
                    Convert.toStr(item.get("file_category")),user);
            config.setFileCategoryId(Convert.toInt(item.get("file_category")));
            config.setFileCategoryName(categoryName);
            config.setRequired(Convert.toBool(item.get("required")));
            config.setTransFormat(Convert.toStr(item.get("trans_format")));
            config.setFormFields(Convert.toList(Long.class,item.get("form_fields")));
            config.setMetadataName(Convert.toStr(item.get("metadata_name")));
            config.setCategoryMark(Convert.toStr(item.get("category_mark")));
            config.setFormPage(Convert.toBool(item.get("is_form_page")));
            result.add(config);
        }

        return result;
    }

   private String  getFieldOptionName(long formId,String fieldName,String fieldValue,SimpleEmployee user) {
        Optional<Long> subFormIdOp = formInfoService.getSubFormId(DatasourceGroupType.WEAVER_EBUILDER_FORM_SERVICE,
                tableName,user.getTenantKey());
        if (!subFormIdOp.isPresent()) {
            throw new FormNotFoundException("获取流程表单文件配置错误，无法获取当前表单id");
        }
        long subFormId = subFormIdOp.get();
        log.debug("subFormId:{}", subFormId);

        Optional<FormFieldEntity> fieldOp = fieldInfoService.getFieldByName(DatasourceGroupType.WEAVER_EBUILDER_FORM_SERVICE,
                formId,subFormId, fieldName);
        if (!fieldOp.isPresent()) {
            throw new FieldNotFoundException("无法查询到字段，字段名称:" + fieldName);
        }
        FormFieldEntity field = fieldOp.get();
        String fieldOptionName = fieldInfoService.getFieldOptionName(DatasourceGroupType.WEAVER_EBUILDER_FORM_SERVICE,
                field.getId(), fieldValue);
        if (fieldOptionName.isEmpty()) {
            throw new ArchiveConfigException("无法找到字段选项名称，字段名称：" + fieldName + "，选项值：" + fieldValue);
        }
        return fieldOptionName;
    }

}
