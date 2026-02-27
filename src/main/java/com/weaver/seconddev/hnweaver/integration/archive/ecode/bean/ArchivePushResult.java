package com.weaver.seconddev.hnweaver.integration.archive.ecode.bean;

import lombok.Data;

/**
 * @author 姚礼林
 * @desc 档案推送结果
 * @date 2025/9/22
 **/
@Data
public class ArchivePushResult {
    private boolean success;
    private String msg;
    private String archiveId;

    public ArchivePushResult() {
    }

    public ArchivePushResult(boolean success, String msg, String archiveId) {
        this.success = success;
        this.msg = msg;
        this.archiveId = archiveId;
    }
}
