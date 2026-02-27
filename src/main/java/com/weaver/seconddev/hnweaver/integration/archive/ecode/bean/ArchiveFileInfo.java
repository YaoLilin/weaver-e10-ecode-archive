package com.weaver.seconddev.hnweaver.integration.archive.ecode.bean;

import lombok.Data;

import java.io.File;

/**
 * @author 姚礼林
 * @desc 档案包内文件信息
 * @date 2025/8/21
 **/
@Data
public class ArchiveFileInfo {
    /**
     * 文件相对于档案包的存放路径
     */
    private String relativeFilePath;
    private String md5;
    /**
     * 文件大小，单位字节
     */
    private Long fileSize;
    private File file;
}
