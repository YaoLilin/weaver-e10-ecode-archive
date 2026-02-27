package com.weaver.seconddev.hnweaver.integration.archive.ecode.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.weaver.framework.rpc.annotation.RpcReference;
import com.weaver.seconddev.hnweaver.common.SqlExecuteClient;
import com.weaver.seconddev.hnweaver.common.bean.SqlExecuteResult;
import com.weaver.seconddev.hnweaver.common.constants.DatasourceGroupType;
import com.weaver.seconddev.hnweaver.common.constants.RpcGroupConstants;
import com.weaver.seconddev.hnweaver.common.exception.SqlExecuteException;
import com.weaver.seconddev.hnweaver.common.util.SqlUtil;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.*;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.config.interfaces.ArchiveApiConfigInterface;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.config.interfaces.ArchiveFormFileConfigInterface;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.config.interfaces.ArchiveMetadataConfigInterface;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.config.interfaces.ArchivePackageConfigInterface;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.exception.ArchiveConfigException;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.util.UserUtil;
import com.weaver.teams.domain.user.SimpleEmployee;
import com.weaver.teams.security.context.UserContext;
import com.weaver.workflow.common.entity.org.WeaUser;
import com.weaver.workflow.common.entity.pathdef.path.base.WfpBaseInfoEntity;
import com.weaver.workflow.pathdef.api.rest.path.WfpPathRest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author 姚礼林
 * @desc 获取建模配置的档案推送配置
 * @date 2025/7/25
 **/
@Component
@RequiredArgsConstructor
@Slf4j
public class ArchiveWorkflowConfigManager extends AbstractArchiveWorkflowConfig {
    private final SqlExecuteClient sqlExecuteClient;
    private final ArchivePackageConfigInterface archivePackageConfigManager;
    private final ArchiveApiConfigInterface apiConfigManager;
    private final ArchiveFormFileConfigInterface formFileConfigManager;
    private final ArchiveMetadataConfigInterface metadataConfigManager;
    private final UserUtil userUtil;
    @Getter
    @Setter
    private String tableName = "uf_archive_wf_config";
    @RpcReference(group = RpcGroupConstants.WORKFLOW)
    private final WfpPathRest wfpPathRest;


    @Override
    protected Optional<Map<String, Object>> getBaseConfigData(long workflowId) {
        String conditionSql = "  WHERE workflow=? AND " + SqlUtil.NO_DELETE;
        String querySql = "SELECT form_data_id,config_name,package_config,archive_api_config " +
                " FROM " + tableName;
        String sql = querySql + conditionSql;
        SqlExecuteResult result = sqlExecuteClient.executeSql(DatasourceGroupType.WEAVER_EBUILDER_APP_SERVICE,
                sql, workflowId);
        if (!result.isSuccess()) {
            throw new SqlExecuteException("查询建模流程配置失败，sql执行异常，sql:" + sql);
        }
        if (result.getRecords().isEmpty()) {
            return getConfigDataWithRearVersion(workflowId, conditionSql, querySql);
        }
        return Optional.of(result.getRecords().get(0));
    }


    /**
     * 获取建模配置，匹配建模中开启了流程后续版本启用的情况，判断当前流程版本是否大于等于建模中配置的流程版本，符合则返回该建模配置数据
     */
    private Optional<Map<String ,Object>> getConfigDataWithRearVersion(long workflowId, String conditionSql,
                                                                         String querySql) {
        conditionSql += " AND enable_after_version=1";
        // 获取当前流程版本
        Optional<Integer> currentWfVersionId = getWorkflowVersionId(workflowId);
        if (!currentWfVersionId.isPresent()) {
            log.error("未找到当前流程的版本号");
            return Optional.empty();
        }
        SimpleEmployee user = userUtil.getUser();
        String sql = querySql + conditionSql;
        List<Long> allVersionWfId = wfpPathRest.getAllVersionWfId(CollUtil.toList(workflowId), user.getTenantKey());
        log.info("所有流程版本的流程id：{}", allVersionWfId);
        for (Long wfId : allVersionWfId) {
            // 查询建模中配置当前流程id的建模数据
            SqlExecuteResult executeResult = sqlExecuteClient.executeSql(DatasourceGroupType.WEAVER_EBUILDER_APP_SERVICE,
                    sql, wfId);
            if (!executeResult.getRecords().isEmpty()) {
                 // 获取建模中配置的流程版本
                Optional<Integer> workflowVersionIdOp = getWorkflowVersionId(wfId);
                // 当前流程版本需要大于等于建模配置的流程版本
                if (workflowVersionIdOp.isPresent() && currentWfVersionId.get() >= workflowVersionIdOp.get()) {
                    return Optional.of(executeResult.getRecords().get(0));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    protected ArchiveWorkflowConfig getBaseConfig(long workflowId,Map<String ,Object> configData) {
        ArchiveWorkflowConfig config = new ArchiveWorkflowConfig();
        config.setConfigName(Convert.toStr(configData.get("config_name")));
        config.setWorkflow(Convert.toLong(configData.get("workflow")));
        config.setEnableAfterVersion(Convert.toBool(configData.get("enable_after_version"),false));
        config.setFormDataId(Convert.toLong(configData.get("form_data_id")));
        return config;
    }

    @Override
    protected List<ArchiveMetadataConfig> getMetadataConfigs(Long formDataId) {
        return metadataConfigManager.getConfig(formDataId);
    }

    @Override
    protected @NotNull ArchivePackageConfig getPackConfig(Long id) {
        if (id == null) {
            throw new ArchiveConfigException("未找到档案打包配置配置，数据id为空，请检查建模是否配置档案打包配置");
        }
        Optional<ArchivePackageConfig> config = archivePackageConfigManager.getConfig(id);
        if (!config.isPresent()) {
            throw new ArchiveConfigException("未找到档案打包配置配置，数据id：" + id);
        }
        return config.get();
    }

    @Override
    protected @NotNull ArchiveApiConfig getArchiveApiConfig(Long apiConfigId) {
        if (apiConfigId == null) {
            throw new ArchiveConfigException("档案传输接口未配置");
        }
        Optional<ArchiveApiConfig> apiConfigOp = apiConfigManager.getConfig(apiConfigId);
        if (!apiConfigOp.isPresent()) {
            throw new ArchiveConfigException("未找到对应的档案传输接口配置，接口id：" + apiConfigId);
        }
        return apiConfigOp.get();
    }

    @Override
    protected List<ArchiveFormFileConfig> getFormFileConfigs(Long formDataId) {
        SimpleEmployee user = userUtil.getUser();
        return formFileConfigManager.getConfig(formDataId,user);
    }

    private Optional<Integer> getWorkflowVersionId(Long wfId) {
        WeaUser weaUser = userUtil.getWeaUserByConfig();
        List<WfpBaseInfoEntity> wfInfoList = wfpPathRest.getBaseInfoByIdS(CollUtil.toList(wfId),
                weaUser,new HashMap<>(1));
        if (CollUtil.isNotEmpty(wfInfoList)) {
            WfpBaseInfoEntity wfInfo = wfInfoList.get(0);
            return Optional.of(wfInfo.getVersionId());
        }
        return Optional.empty();
    }

}
