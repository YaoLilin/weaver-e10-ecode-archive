package com.weaver.seconddev.hnweaver.integration.archive.ecode.exception;

/**
 * @author 姚礼林
 * @desc 档案元数据异常
 * @date 2025/8/19
 **/
public class ArchiveMetadataException extends RuntimeException {
    public ArchiveMetadataException(String message) {
        super(message);
    }

    public ArchiveMetadataException(String message, Throwable cause) {
        super(message, cause);
    }

}
