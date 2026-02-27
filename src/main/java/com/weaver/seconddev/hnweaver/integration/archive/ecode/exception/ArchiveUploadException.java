package com.weaver.seconddev.hnweaver.integration.archive.ecode.exception;

/**
 * @author 姚礼林
 * @desc 档案包上传异常
 * @date 2025/9/1
 **/
public class ArchiveUploadException extends RuntimeException {
    public ArchiveUploadException(String message) {
        super(message);
    }

    public ArchiveUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
