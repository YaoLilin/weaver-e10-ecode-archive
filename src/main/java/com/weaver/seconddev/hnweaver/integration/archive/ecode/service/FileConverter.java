package com.weaver.seconddev.hnweaver.integration.archive.ecode.service;

import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.FileConvertParam;

/**
 * @author 姚礼林
 * @desc 文件格式转换
 * @date 2025/8/19
 **/
public interface FileConverter {

    /**
     * 转换文件格式
     * @param param 转换参数
     * @return 是否成功
     */
    boolean convert(FileConvertParam param);
}
