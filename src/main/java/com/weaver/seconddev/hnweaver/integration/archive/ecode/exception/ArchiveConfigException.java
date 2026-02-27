package com.weaver.seconddev.hnweaver.integration.archive.ecode.exception;

/**
 * @author 姚礼林
 * @desc 档案配置异常
 * @date 2025/8/21
 **/
public class ArchiveConfigException extends RuntimeException {

    public ArchiveConfigException(String message) {
        super(message);
    }

    public ArchiveConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
