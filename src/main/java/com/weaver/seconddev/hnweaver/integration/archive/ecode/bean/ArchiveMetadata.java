package com.weaver.seconddev.hnweaver.integration.archive.ecode.bean;

import lombok.Data;

/**
 * @author 姚礼林
 * @desc 档案元数据
 * @date 2025/8/18
 **/
@Data
public class ArchiveMetadata {
    private String name;
    private String value;
    /**
     * 是否是文件元数据，如果是文件元数据，则在元数据xml文件中会以不同的形式展现
     */
    private boolean file = false;
    /**
     * 文件元数据信息
     */
    private MetadataFileInfo fileInfo;

    public ArchiveMetadata() {
    }

    public ArchiveMetadata(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static ArchiveMetadata createFileMetadata(String metadataName, MetadataFileInfo fileInfo) {
        ArchiveMetadata metadata = new ArchiveMetadata();
        metadata.setName(metadataName);
        metadata.setFile(true);
        metadata.setFileInfo(fileInfo);

        return metadata;
    }
}
