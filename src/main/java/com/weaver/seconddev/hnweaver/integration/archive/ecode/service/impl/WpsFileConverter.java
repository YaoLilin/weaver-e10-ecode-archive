package com.weaver.seconddev.hnweaver.integration.archive.ecode.service.impl;

import com.weaver.common.base.entity.result.WeaResult;
import com.weaver.eteams.file.client.file.FileCapabilityParam;
import com.weaver.eteams.file.client.file.FileCapabilityResult;
import com.weaver.eteams.file.client.param.FileCovertParam;
import com.weaver.eteams.file.client.remote.OfficialFileConvertService;
import com.weaver.file.ud.api.FileDownloadService;
import com.weaver.framework.rpc.annotation.RpcReference;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.FileConvertParam;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.exception.FileConvertException;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.service.FileConverter;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.util.UserUtil;
import com.weaver.teams.security.context.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author 姚礼林
 * @desc 使用wps对文件进行转换
 * @date 2025/8/19
 **/
@Slf4j
@RequiredArgsConstructor
@Component
public class WpsFileConverter implements FileConverter {
    @RpcReference
    private final OfficialFileConvertService fileConvertService;
    private final FileDownloadService fileDownloadService;
    private final UserUtil userUtil;

    @Override
    public boolean convert(FileConvertParam param) {
        log.info("转换文件，文件路径：{}，转换目标格式：{}", param.getFilePath(), param.getTargetFormat());
        String convertedFileId = convertFile(param.getTargetFormat(), param.getFileId());
        log.info("转换后的文件id：{}", convertedFileId);

        InputStream inputStream = fileDownloadService.downloadFile(Long.parseLong(convertedFileId)).getInputStream();
        try {
            Files.copy(inputStream, Paths.get(param.getSaveFilePath()));
        } catch (IOException e) {
            log.error("保存转换后的文件失败", e);
            return false;
        }
        log.info("文件保存成功，路径：{}", param.getSaveFilePath());

        return true;
    }

    private String convertFile(String targetFormat, long fileId) {
        FileCapabilityParam param = new FileCapabilityParam();
        param.setOption("CONVERT");
        param.setModule("doc");
        param.setUserId(userUtil.getUser().getEmployeeId().toString());
        FileCovertParam fileCovertParam = new FileCovertParam();
        fileCovertParam.setTargetType(targetFormat);
        param.setFileCovertParam(fileCovertParam);
        param.setFileId(fileId);

        WeaResult<FileCapabilityResult> result = fileConvertService.capabilityFile(param);
        if (result.isFail()) {
            throw new FileConvertException("文件转换失败，错误信息：" + result.getMsg());
        }
        log.info("文件转换成功");
        FileCapabilityResult data = result.getData();
        return data.getTargetFileId();
    }
}
