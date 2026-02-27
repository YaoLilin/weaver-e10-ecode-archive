package com.weaver.seconddev.hnweaver.integration.archive.ecode.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.weaver.seconddev.hnweaver.common.SqlExecuteClient;
import com.weaver.seconddev.hnweaver.common.bean.SqlExecuteResult;
import com.weaver.seconddev.hnweaver.common.constants.DatasourceGroupType;
import com.weaver.seconddev.hnweaver.common.exception.SqlExecuteException;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.service.ArchiveLogService;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.service.ArchiveRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 姚礼林
 * @desc 档案日志服务实现类，用于处理档案推送相关的日志查询
 * @date 2025/10/16
 **/
@RequiredArgsConstructor
@Service
@Slf4j
public class ArchiveLogServiceImpl implements ArchiveLogService {
    private final SqlExecuteClient sqlExecuteClient;
    private final ArchiveRecordService archiveRecordService;

    @Override
    public List<String> getLogIdsByRequestIds(List<String> requestIds) {
        String sql = buildInQuerySql(Convert.toList(Long.class, requestIds));
        log.debug("sql:{}", sql);

        SqlExecuteResult result = sqlExecuteClient.executeSql(DatasourceGroupType.WEAVER_ARCHIVE_CORE_SERVICE, sql);
        if (!result.isSuccess()) {
            throw new SqlExecuteException("sql 查询错误");
        }

        return result.getRecords().stream().map(i -> Convert.toStr(i.get("ID"))).collect(Collectors.toList());
    }

    @Override
    public List<String> getAllFailedLogIds() {
        List<Long> allFailedRequestIds = archiveRecordService.getAllFailedRequestIds();
        log.debug("所有失败的请求id数量：{}", allFailedRequestIds.size());

        if (allFailedRequestIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 使用分批查询，每批最多1000个ID（根据数据库性能调整）
        return getLogIdsByRequestIdsBatch(allFailedRequestIds, 1000);
    }

    /**
     * 分批查询日志ID
     * @param requestIds 请求ID列表
     * @param batchSize 每批数量
     * @return 日志ID列表
     */
    private List<String> getLogIdsByRequestIdsBatch(List<Long> requestIds, int batchSize) {
        List<String> allLogIds = new java.util.ArrayList<>();
        int total = requestIds.size();

        // 分批处理
        for (int i = 0; i < total; i += batchSize) {
            int end = Math.min(i + batchSize, total);
            List<Long> batch = requestIds.subList(i, end);

            log.debug("正在处理第 {}/{} 批，数量：{}", (i / batchSize + 1),
                     (total + batchSize - 1) / batchSize, batch.size());

            // 构建 IN 查询
            String sql = buildInQuerySql(batch);
            SqlExecuteResult result = sqlExecuteClient.executeSql(
                DatasourceGroupType.WEAVER_ARCHIVE_CORE_SERVICE, sql);

            if (!result.isSuccess()) {
                log.error("批次查询失败，跳过该批次。批次范围：{}-{}", i, end);
                continue;
            }

            // 收集结果
            List<String> batchLogIds = result.getRecords().stream()
                .map(rec -> Convert.toStr(rec.get("ID")))
                .collect(Collectors.toList());
            allLogIds.addAll(batchLogIds);
        }

        log.info("共查询到 {} 条日志ID", allLogIds.size());
        return allLogIds;
    }

    /**
     * 构建 IN 查询 SQL
     * @param requestIds 请求ID列表
     * @return SQL语句
     */
    private String buildInQuerySql(List<Long> requestIds) {
        return "SELECT ID FROM ias_data_log WHERE DATA_ID IN (" + requestIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")) +
                ")";
    }
}
