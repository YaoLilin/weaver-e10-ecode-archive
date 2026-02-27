package com.weaver.seconddev.hnweaver.integration.archive.ecode.domaim.dto;

import com.weaver.seconddev.hnweaver.integration.archive.ecode.constants.ArchiveResultStatus;
import lombok.Data;

import java.io.File;

/**
 * @author 姚礼林
 * @desc 档案推送记录台账数据
 * @date 2025/9/17
 **/
@Data
public class ArchiveRecordDto {
    private Long workflow;
    private Long requestId;
    private ArchiveResultStatus status;
    private String feedbackMsg;
    private String pushedTime;
    private File archivePackage;
    private String msg;
}
