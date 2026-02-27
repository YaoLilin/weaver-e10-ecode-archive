package com.weaver.seconddev.hnweaver.integration.archive.ecode.exception;

/**
 * @author 姚礼林
 * @desc 创建xml文件异常
 * @date 2025/8/20
 **/
public class CreateXmlException extends RuntimeException{
    public CreateXmlException() {
    }

    public CreateXmlException(String message) {
        super(message);
    }

    public CreateXmlException(String message, Throwable cause) {
        super(message, cause);
    }
}
