package com.weaver.seconddev.hnweaver.integration.archive.ecode.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.FormFilePackageConfig;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author 姚礼林
 * @desc 文件名称处理
 * @date 2025/8/21
 **/
@Component
public class FileNameHandler {

    public String handle(String fileName, FormFilePackageConfig formFilePackageConfig) {
        String newFileName = fileName;
        String suffix = FileUtil.getSuffix(fileName);
        if (formFilePackageConfig.isUuidEnable()) {
            newFileName = UUID.randomUUID() + "." + suffix;
        }
        if (CharSequenceUtil.isNotBlank(formFilePackageConfig.getFilePrefix())) {
            newFileName = formFilePackageConfig.getFilePrefix() + newFileName;
        }
        if (CharSequenceUtil.isNotBlank(formFilePackageConfig.getFileSuffix())) {
            String name = FileUtil.getName(fileName);
            newFileName = name + formFilePackageConfig.getFileSuffix() + "." + suffix;
        }
        return newFileName;
    }
}
