package com.weaver.seconddev.hnweaver.integration.archive.ecode.service;

import java.io.File;
import java.util.Map;

/**
 * @author 姚礼林
 * @desc xml 文件生成，根据模板生成xml文件
 * @date 2025/8/20
 **/
public interface XmlGenerator {

    /**
     * 生成xml文件
     * @param data xml模板数据
     * @param xmlTemplate xml模板
     * @param savePath 文件保存路径
     * @return xml文件
     */
    File createXml(Map<String, Object> data, String xmlTemplate, String savePath);
}
