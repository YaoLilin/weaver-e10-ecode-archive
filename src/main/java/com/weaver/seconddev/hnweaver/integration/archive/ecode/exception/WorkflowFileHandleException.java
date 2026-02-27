package com.weaver.seconddev.hnweaver.integration.archive.ecode.exception;

/**
 * @author 姚礼林
 * @desc 流程文件处理异常
 * @date 2025/8/19
 **/
public class WorkflowFileHandleException extends RuntimeException{
    public WorkflowFileHandleException() {
    }

    public WorkflowFileHandleException(String message) {
        super(message);
    }

    public WorkflowFileHandleException(String message, Throwable cause) {
        super(message, cause);
    }
}
