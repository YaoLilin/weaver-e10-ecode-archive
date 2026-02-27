package com.weaver.seconddev.hnweaver.integration.archive.ecode.exception;

/**
 * @author 姚礼林
 * @desc 档案文件处理异常
 * @date 2025/8/21
 **/
public class ArchiveFileHandleException extends RuntimeException{
    public ArchiveFileHandleException() {
    }

    public ArchiveFileHandleException(String message) {
        super(message);
    }

    public ArchiveFileHandleException(String message, Throwable cause) {
        super(message, cause);
    }
}
