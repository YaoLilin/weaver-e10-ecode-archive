package com.weaver.seconddev.hnweaver.integration.archive.ecode.config;

import cn.hutool.core.convert.Convert;
import com.weaver.seconddev.hnweaver.common.SqlExecuteClient;
import com.weaver.seconddev.hnweaver.common.bean.SqlExecuteResult;
import com.weaver.seconddev.hnweaver.common.constants.DatasourceGroupType;
import com.weaver.seconddev.hnweaver.common.exception.SqlExecuteException;
import com.weaver.seconddev.hnweaver.common.util.SqlUtil;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveMetadataConfig;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.config.interfaces.ArchiveMetadataConfigInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 姚礼林
 * @desc 获取档案元数据配置
 * @date 2025/9/3
 **/
@Component
@RequiredArgsConstructor
@Slf4j
public class ArchiveMetadataConfigManager implements ArchiveMetadataConfigInterface {
    @Getter
    @Setter
    private String tableName = "uf_archive_mxb1";
    private final SqlExecuteClient sqlExecuteClient;

    @Override
    public List<ArchiveMetadataConfig> getConfig(long formDataId) {
        List<Map<String, Object>> configData = getConfigData(formDataId);
        if (configData.isEmpty()) {
            log.warn("未找到元数据配置，数据id：{}", formDataId);
        }

        return buildMetadataConfig(configData);
    }


    private List<Map<String, Object>> getConfigData(long id) {
        String sql = "SELECT d.metadata_name,d.form_field,d.metadata_show_name,d.required,d.fix_value FROM " +
                tableName + " d WHERE d.form_data_id=? AND " + SqlUtil.NO_DELETE;
        SqlExecuteResult result = sqlExecuteClient.executeSql(DatasourceGroupType.WEAVER_EBUILDER_APP_SERVICE, sql, id);
        if (!result.isSuccess()) {
            throw new SqlExecuteException("查询元数据配置失败，sql执行异常，sql:" + sql);
        }

        if (result.getRecords().isEmpty()) {
            log.warn("未查询到数据");
            return Collections.emptyList();
        }
        return result.getRecords();
    }

    private List<ArchiveMetadataConfig> buildMetadataConfig(List<Map<String, Object>> records) {
        return records.stream().map(i -> {
            ArchiveMetadataConfig metadataConfig = new ArchiveMetadataConfig();
            metadataConfig.setMetadataName(Convert.toStr(i.get("metadata_name")));
            metadataConfig.setFormField(Convert.toLong(i.get("form_field")));
            metadataConfig.setMetadataShowName(Convert.toStr(i.get("metadata_show_name")));
            metadataConfig.setRequired(Convert.toBool(i.get("required"), false));
            metadataConfig.setFixValue(Convert.toStr(i.get("fix_value")));
            return metadataConfig;
        }).collect(Collectors.toList());
    }
}
