package com.weaver.seconddev.hnweaver.integration.archive.ecode.exception;

/**
 * @author 姚礼林
 * @desc 档案推送异常
 * @date 2025/9/2
 **/
public class ArchiveException extends RuntimeException {
    public ArchiveException(String message) {
        super(message);
    }
    public ArchiveException(String message, Throwable cause) {
        super(message, cause);
    }
}
