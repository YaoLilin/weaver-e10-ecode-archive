package com.weaver.seconddev.hnweaver.integration.archive.ecode.config;

import cn.hutool.core.convert.Convert;
import com.weaver.seconddev.hnweaver.common.SqlExecuteClient;
import com.weaver.seconddev.hnweaver.common.bean.SqlExecuteResult;
import com.weaver.seconddev.hnweaver.common.constants.DatasourceGroupType;
import com.weaver.seconddev.hnweaver.common.exception.SqlExecuteException;
import com.weaver.seconddev.hnweaver.common.util.SqlUtil;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveApiConfig;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.config.interfaces.ArchiveApiConfigInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import java.util.Map;
import java.util.Optional;

/**
 * @author 姚礼林
 * @desc 获取档案推送接口配置
 * @date 2025/9/1
 **/
@Slf4j
@RequiredArgsConstructor
@Component
public class ArchiveApiConfigManager implements ArchiveApiConfigInterface {
    @Setter
    @Getter
    private String mainTableName = "uf_archive_transfer_api_config";
    private final SqlExecuteClient sqlExecuteClient;

    @Override
    public Optional<ArchiveApiConfig> getConfig(long id) {
        Optional<Map<String, Object>> configDataOp = getConfigData(id);
        if (!configDataOp.isPresent()) {
            return Optional.empty();
        }
        Map<String, Object> configData = configDataOp.get();
        return Optional.of(buildApiConfig(configData));
    }

    private Optional<Map<String, Object>> getConfigData(long id) {
        String querySql = "SELECT config_name,upload_ftp_enable,ftp_address,ftp_port,ftp_username,ftp_password," +
                "ftp_file_path FROM " + mainTableName + " WHERE id=? AND " + SqlUtil.NO_DELETE;
        SqlExecuteResult result = sqlExecuteClient.executeSql(DatasourceGroupType.WEAVER_EBUILDER_APP_SERVICE,
                querySql, id);
        if (!result.isSuccess()) {
            throw new SqlExecuteException("查询档案接口配置主表失败,sql:" + querySql);
        }
        if (result.getRecords().isEmpty()) {
            log.warn("未找到主表数据");
            return Optional.empty();
        }
        return Optional.of(result.getRecords().get(0));
    }

    private ArchiveApiConfig buildApiConfig(Map<String, Object> configData) {
        ArchiveApiConfig apiConfig = new ArchiveApiConfig();
        apiConfig.setConfigName(Convert.toStr(configData.get("config_name")));
        apiConfig.setUploadFtpEnable(Convert.toBool(configData.get("upload_ftp_enable"), false));
        apiConfig.setFtpAddress(Convert.toStr(configData.get("ftp_address")));
        apiConfig.setFtpPort(Convert.toInt(configData.get("ftp_port")));
        apiConfig.setFtpUsername(Convert.toStr(configData.get("ftp_username")));
        apiConfig.setFtpPassword(Convert.toStr(configData.get("ftp_password")));
        apiConfig.setFtpFilePath(Convert.toStr(configData.get("ftp_file_path")));
        return apiConfig;
    }
}
