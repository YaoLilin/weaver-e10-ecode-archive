package com.weaver.seconddev.hnweaver.integration.archive.ecode.config;

import cn.hutool.core.convert.Convert;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.*;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.config.interfaces.ArchiveWorkflowConfigInterface;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.exception.ArchiveConfigException;
import com.weaver.teams.domain.user.SimpleEmployee;
import com.weaver.teams.security.context.UserContext;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author 姚礼林
 * @desc 获取流程档案推送配置抽象类
 * @date 2025/9/3
 **/
@RequiredArgsConstructor
public abstract class AbstractArchiveWorkflowConfig implements ArchiveWorkflowConfigInterface {

    /**
     * 获取档案推送配置
     * @param workflowId 流程id
     * @return 档案推送配置
     */
    @Override
    public final Optional<ArchiveWorkflowConfig> getConfig(long workflowId) {
        try {
            Optional<Map<String, Object>> configDataOp = getBaseConfigData(workflowId);
            if (!configDataOp.isPresent()) {
                return Optional.empty();
            }
            Map<String, Object> configData = configDataOp.get();
            ArchiveWorkflowConfig config = getBaseConfig(workflowId,configData);
            Long formDataId = config.getFormDataId();
            List<ArchiveMetadataConfig> metadataConfigs = getMetadataConfigs(formDataId);
            config.setMetadataConfigs(metadataConfigs);
            ArchivePackageConfig packageConfig = getPackConfig(Convert.toLong(configData.get("package_config")));
            config.setPackageConfig(packageConfig);
            Long apiConfigId = Convert.toLong(configData.get("archive_api_config"));
            ArchiveApiConfig archiveApiConfig = getArchiveApiConfig(apiConfigId);
            config.setApiConfig(archiveApiConfig);
            List<ArchiveFormFileConfig> archiveFormFileConfigList = getFormFileConfigs(formDataId);
            config.setFormFileConfigs(archiveFormFileConfigList);

            return Optional.of(config);
        } catch (Exception e) {
            throw new ArchiveConfigException("获取档案推送配置失败，信息："+e.getMessage(),e);
        }
    }

    /**
     * 获取建模主表字段数据，这些数据包括了基础的档案推送配置
     * @param workflowId 流程id
     * @return 建模主表字段数据
     */
    protected abstract Optional<Map<String, Object>> getBaseConfigData(long workflowId);

    /**
     * 获取基础的档案推送配置，也就是建模的主表数据，不包括对象中嵌套的档案推送配置
     * @param workflowId 流程id
     * @param configData 建模主表数据
     * @return 基础的档案推送配置
     */
    protected abstract ArchiveWorkflowConfig getBaseConfig(long workflowId,Map<String ,Object> configData);

    /**
     * 获取档案元数据配置
     * @param formDataId 档案推送配置建模主表数据id
     * @return 档案元数据配置
     */
    protected abstract List<ArchiveMetadataConfig> getMetadataConfigs(Long formDataId);

    /**
     * 获取档案打包配置
     * @param packageConfig 档案打包配置id
     * @return  档案打包配置
     */
    protected abstract ArchivePackageConfig getPackConfig(Long packageConfig);

    /**
     * 获取档案推送接口配置
     * @param apiConfigId 档案推送接口配置id
     * @return 档案推送接口配置
     */
    protected abstract ArchiveApiConfig getArchiveApiConfig(Long apiConfigId);

    /**
     * 获取档案推送流程表单文件配置
     * @param formDataId 档案推送配置建模主表数据id
     * @return 档案推送流程表单文件配置
     */
    protected abstract List<ArchiveFormFileConfig> getFormFileConfigs(Long formDataId);


}
