package com.weaver.seconddev.hnweaver.integration.archive.ecode.service;

import com.weaver.seconddev.hnweaver.integration.archive.ecode.domaim.dto.ArchiveRecordDto;

import java.util.List;

/**
 * @author 姚礼林
 * @desc 档案推送记录建模表操作
 * @date 2025/9/8
 **/
public interface ArchiveRecordService {

    /**
     * 更新或插入归档记录
     * @param recordDto 更新数据
     * @param insert    条件不满足是否插入
     * @return 执行结果
     */
    boolean updateOrInsertRecord(ArchiveRecordDto recordDto, boolean insert);

    /**
     * 根据档案数据包id判断台账是否存在档案推送记录
     * @param archiveId 档案数据包id，为流程请求id
     * @return 是否存在记录
     */
    boolean existRecord(String archiveId);

    /**
     * 获取所有失败的档案推送请求id
     * @return 失败的档案推送请求id
     */
    List<Long> getAllFailedRequestIds();
}
