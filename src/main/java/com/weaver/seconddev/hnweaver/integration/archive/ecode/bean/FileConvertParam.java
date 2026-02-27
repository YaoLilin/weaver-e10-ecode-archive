package com.weaver.seconddev.hnweaver.integration.archive.ecode.bean;

import lombok.Data;

/**
 * @author 姚礼林
 * @desc 文件转换参数
 * @date 2025/8/20
 **/
@Data
public class FileConvertParam {
    private String filePath;
    private String saveFilePath;
    private String targetFormat;
    private Long fileId;

    public FileConvertParam(String filePath, String saveFilePath, String targetFormat, Long fileId) {
        this.filePath = filePath;
        this.saveFilePath = saveFilePath;
        this.targetFormat = targetFormat;
        this.fileId = fileId;
    }
}
