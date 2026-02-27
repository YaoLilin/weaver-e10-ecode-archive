package com.weaver.seconddev.hnweaver.integration.archive.ecode.service;

import java.util.List;

/**
 * @author 姚礼林
 * @desc 档案归档日志业务类
 * @date 2025/10/16
 **/
public interface ArchiveLogService {

    /**
     * 根据流程请求id查询标准模块中的档案归档日志id，这些日志id可用于执行标准的档案推送
     *
     * @param requestIds 流程请求id
     * @return 归档日志id集合
     */
    List<String> getLogIdsByRequestIds(List<String> requestIds);

    /**
     * 查询所有失败的归档日志id
     * @return 所有失败的归档日志id
     */
    List<String> getAllFailedLogIds();
}
