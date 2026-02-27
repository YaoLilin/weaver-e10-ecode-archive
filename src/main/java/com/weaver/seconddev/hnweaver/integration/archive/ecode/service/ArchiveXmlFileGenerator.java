package com.weaver.seconddev.hnweaver.integration.archive.ecode.service;

import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveXmlFileGenerateParam;

import java.io.File;

/**
 * @author 姚礼林
 * @desc 生成元数据xml文件
 * @date 2025/8/20
 **/
public interface ArchiveXmlFileGenerator {

    /**
     * 创建档案包中的xml文件
     * @param param 相关参数，可获取到档案推送相关信息
     * @return xml文件
     */
    File createXml(ArchiveXmlFileGenerateParam param);
}
