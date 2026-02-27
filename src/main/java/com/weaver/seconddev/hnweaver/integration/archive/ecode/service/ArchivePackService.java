package com.weaver.seconddev.hnweaver.integration.archive.ecode.service;

import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchivePackParam;

import java.io.File;

/**
 * @author 姚礼林
 * @desc 打包成档案包
 * @date 2025/9/1
 **/
public interface ArchivePackService {

    /**
     * 进行打包，生成档案包文件
     * @param param 相关参数
     * @return 档案包文件
     */
    File pack(ArchivePackParam param);
}
