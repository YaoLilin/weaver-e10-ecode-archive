package com.weaver.seconddev.hnweaver.integration.archive.ecode.bean;

import lombok.Data;

/**
 * @author 姚礼林
 * @desc 档案元数据文件信息
 * @date 2025/8/18
 **/
@Data
public class MetadataFileInfo {
    private String fileName;
    /**
     * 文件类型（扩展名）
     */
    private String fileType;
    private String  fileCategoryName;
    private String fileCategoryMark;
}
