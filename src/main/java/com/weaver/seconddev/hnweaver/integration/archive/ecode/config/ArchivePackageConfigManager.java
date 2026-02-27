package com.weaver.seconddev.hnweaver.integration.archive.ecode.config;

import cn.hutool.core.convert.Convert;
import com.weaver.seconddev.hnweaver.common.SqlExecuteClient;
import com.weaver.seconddev.hnweaver.common.bean.SqlExecuteResult;
import com.weaver.seconddev.hnweaver.common.constants.DatasourceGroupType;
import com.weaver.seconddev.hnweaver.common.exception.SqlExecuteException;
import com.weaver.seconddev.hnweaver.common.util.SqlUtil;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchivePackageConfig;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.FormFilePackageConfig;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.config.interfaces.ArchivePackageConfigInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author 姚礼林
 * @desc 获取档案打包配置
 * @date 2025/8/21
 **/
@Slf4j
@RequiredArgsConstructor
@Component
public class ArchivePackageConfigManager implements ArchivePackageConfigInterface {
    @Getter
    @Setter
    private String mainTableName = "uf_archive_pack_conf";
    @Getter
    @Setter
    private String formFilePackConfigDetailTableName = "uf_archive_pk_conf_mxb1";
    private final SqlExecuteClient sqlExecuteClient;

    @Override
    public Optional<ArchivePackageConfig> getConfig(long id) {
        Optional<Map<String, Object>> configDataOp = getConfigData(id);
        if (!configDataOp.isPresent()) {
            return Optional.empty();
        }
        Map<String, Object> configData = configDataOp.get();
        long formDataId = Convert.toLong(configData.get("form_data_id"));
        ArchivePackageConfig packageConfig = convertToPackageConfig(configData);
        List<Map<String, Object>> formFilePackConfigDetailData = getFormFilePackConfigDetailData(formDataId);
        List<FormFilePackageConfig> formFilePackageConfigs = convertToFormFilePackageConfig(formFilePackConfigDetailData);
        packageConfig.setFormFilePackageConfigs(formFilePackageConfigs);

        return Optional.of(packageConfig);
    }

    protected ArchivePackageConfig convertToPackageConfig(Map<String, Object> data) {
        ArchivePackageConfig packageConfig = new ArchivePackageConfig();

        packageConfig.setPackageName(Convert.toStr(data.get("package_name")));
        packageConfig.setConfigName(Convert.toStr(data.get("config_name")));
        packageConfig.setMetadataXmlName(Convert.toStr(data.get("metadata_xml_name")));
        packageConfig.setMetadataXmlPath(Convert.toStr(data.get("metadata_xml_path")));
        packageConfig.setPackageXmlName(Convert.toStr(data.get("package_xml_name")));
        packageConfig.setPackageXmlPath(Convert.toStr(data.get("package_xml_path")));
        packageConfig.setFormFilePath(Convert.toStr(data.get("form_file_path")));
        packageConfig.setMetadataXmlTemplate(Convert.toStr(data.get("metadata_xml_templat")));
        packageConfig.setPackageXmlTemplate(Convert.toStr(data.get("package_xml_template")));
        return packageConfig;
    }

    private Optional<Map<String, Object>> getConfigData(long id) {
        String querySql = "SELECT z.package_name,z.config_name,z.metadata_xml_name,z.metadata_xml_path," +
                "z.package_xml_name,z.package_xml_path,z.form_file_path,z.metadata_xml_templat,z.package_xml_template, " +
                "z.form_data_id FROM " + mainTableName + " z WHERE z.id=? AND z." + SqlUtil.NO_DELETE;
        SqlExecuteResult sqlResult = sqlExecuteClient.executeSql(DatasourceGroupType.WEAVER_EBUILDER_APP_SERVICE,
                querySql, id);
        if (!sqlResult.isSuccess()) {
            throw new SqlExecuteException("查询档案打包配置主表失败,sql:" + querySql);
        }
        if (sqlResult.getRecords().isEmpty()) {
            log.info("未找到主表数据");
            return Optional.empty();
        }
        return Optional.of(sqlResult.getRecords().get(0));
    }

    private List<Map<String, Object>> getFormFilePackConfigDetailData(long formDataId) {
        String sql = "SELECT file_category,file_path,file_prefix,file_suffix,uuid_enable FROM "
                + formFilePackConfigDetailTableName + " WHERE form_data_id=? AND " + SqlUtil.NO_DELETE;
        SqlExecuteResult sqlResult = sqlExecuteClient.executeSql(DatasourceGroupType.WEAVER_EBUILDER_APP_SERVICE,
                sql, formDataId);
         if (!sqlResult.isSuccess()) {
            throw new SqlExecuteException("查询档案打包配置明细表失败,sql:" + sql);
        }
         return sqlResult.getRecords();
    }

    private List<FormFilePackageConfig> convertToFormFilePackageConfig(List<Map<String, Object>> records) {
        List<FormFilePackageConfig> formFilePackageConfigs = new ArrayList<>();
        for (Map<String, Object> row : records) {
            FormFilePackageConfig formFilePackageConfig = new FormFilePackageConfig();
            formFilePackageConfig.setFileCategory(Convert.toInt(row.get("file_category")));
            formFilePackageConfig.setFilePath(Convert.toStr(row.get("file_path")));
            formFilePackageConfig.setFilePrefix(Convert.toStr(row.get("file_prefix")));
            formFilePackageConfig.setFileSuffix(Convert.toStr(row.get("file_suffix")));
            formFilePackageConfig.setUuidEnable(Convert.toBool(row.get("uuid_enable")));
            formFilePackageConfigs.add(formFilePackageConfig);
        }
        return formFilePackageConfigs;
    }
}
