package com.weaver.seconddev.hnweaver.integration.archive.ecode.exception;

/**
 * @author 姚礼林
 * @desc 档案打包异常
 * @date 2025/9/1
 **/
public class ArchivePackException extends RuntimeException {
    public ArchivePackException(String message) {
        super(message);
    }

    public ArchivePackException(String message, Throwable cause) {
        super(message, cause);
    }
}
