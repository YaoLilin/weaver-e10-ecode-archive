package com.weaver.seconddev.hnweaver.integration.archive.ecode;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.weaver.intcenter.ias.core.api.hook.dto.EcodePushRequest;
import com.weaver.seconddev.hnweaver.common.bean.ResultAndMsg;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.*;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.context.*;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.config.ArchiveProperties;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.exception.ArchiveException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * @author 姚礼林
 * @desc ecode自定义档案推送抽象类 <br>
 * 实现功能：
 * <ul
 * <li> 配置化：从eb表单获取流程档案推送配置，包括文件配置、元数据配置、接口配置等</li>
 * <li> 获取流程表单文件</li>
 * <li>  生成元数据xml文件    </li>
 * <li>  生成数据包封套xml文件</li>
 * <li>  生成档案包zip文件</li>
 * <li>  上传档案包到服务器</li>
 * <li>  调用档案系统推送接口</li>
 * </ul>
 * @date 2025/8/18
 **/
@Getter
@Slf4j
public abstract class AbstractEcodeArchivePushManage {
    private final ArchiveProperties archiveProperties;

    /**
     * 构造函数
     * @param archiveProperties 档案配置属性
     */
    protected AbstractEcodeArchivePushManage(ArchiveProperties archiveProperties) {
        this.archiveProperties = archiveProperties;
    }

    /**
     * 执行档案推送
     * @param dataModel 传入的参数，包括了流程表单数据
     * @return 推送结果
     */
    public final ArchivePushResult doPush(ArchiveDataModel dataModel) {
        long startTime = System.currentTimeMillis();
        String tempDir = getTempPath(new TempPathContext(dataModel));
        try {
            log.debug("临时目录：{}", tempDir);
            ArchivePushContext context = new ArchivePushContext();
            context.setDataModel(dataModel);
            context.setTempDir(tempDir);
            log.debug("参数：{}", JSON.toJSONString(dataModel));
            log.debug("流程请求id：{}", dataModel.getRequestId());
            createTempDir(tempDir);
            // 获取流程档案推送配置
            ArchiveWorkflowConfig workflowConfig = getWorkflowConfig(new GetWorkflowConfigContext(dataModel));
            if (workflowConfig == null) {
                return new ArchivePushResult(false, "未找到对应的流程配置，流程id：" + dataModel.getWorkflowId()
                        , dataModel.getRequestId());
            }
            context.setWorkflowConfig(workflowConfig);
            // 获取流程表单文件
            List<WorkflowFileInfo> workflowFileInfoList =
                    getWorkflowFiles(GetWorkflowFilesContext.from(context));
            context.setWorkflowFileInfoList(workflowFileInfoList);
            // 生成档案元数据
            List<ArchiveMetadata> archiveMetadata =
                    getArchiveMetadata(GetArchiveMetadataContext.from(context));
            context.setArchiveMetadata(archiveMetadata);
            // 生成档案元数据xml文件
            File metadataXmlFile = createMetadataXmlFile(CreateMetadataXmlFileContext.from(context));
            context.setMetadataXmlFile(metadataXmlFile);
            // 打包档案推送包
            File packageZip = packArchivePackage(PackArchivePackageContext.from(context));
            context.setPackageZip(packageZip);
            // 上传档案包
            boolean upload = uploadPackage(packageZip, context);
            if (!upload) {
                return new ArchivePushResult(false, "档案包上传失败", dataModel.getRequestId());
            }
            if (!callApi(context)) {
                return new ArchivePushResult(false, "调用档案系统推送接口失败", dataModel.getRequestId());
            }

            whenPushFinished(context);
            return new ArchivePushResult(true, "推送成功",dataModel.getRequestId());
        } catch (Exception e) {
            log.error("档案推送失败", e);
            return new ArchivePushResult(false, "档案推送失败:" + e.getMessage(), dataModel.getRequestId());
        }finally {
            log.info("推送耗时：{}", System.currentTimeMillis() - startTime);
            if ("1".equals(archiveProperties.getDeleteTempFiles())) {
                if (Files.exists(Paths.get(tempDir))) {
                    if (!FileUtil.del(tempDir)) {
                        log.error("删除临时文件目录失败");
                    }
                }
            }
        }
    }

    /**
     * 获取存放临时文件的目录
     * @param context 上下文参数
     * @return 临时目录
     */
    protected abstract  @NotNull String getTempPath(TempPathContext context) ;

    /**
     * 获取流程表单文件
     * @param context 上下文参数
     * @return 流程表单文件
     */
    protected abstract  List<WorkflowFileInfo> getWorkflowFiles(GetWorkflowFilesContext context);

    /**
     * 获取档案元数据
     * @param context 上下文参数
     * @return 元数据列表
     */
    protected abstract  List<ArchiveMetadata> getArchiveMetadata(GetArchiveMetadataContext context);

    /**
     * 获取流程档案推送配置
     * @param context 上下文参数
     * @return 档案推送配置
     */
    protected abstract  @Nullable ArchiveWorkflowConfig getWorkflowConfig(GetWorkflowConfigContext context);

    /**
     * 创建档案元数据xml文件
     * @param context 上下文参数
     * @return 元数据xml文件
     */
    protected abstract  File createMetadataXmlFile(CreateMetadataXmlFileContext context);


    /**
     * 将所有文件打包成档案包
     * @param context 上下文参数
     * @return 档案包文件
     */
    protected abstract  File packArchivePackage(PackArchivePackageContext context);

    /**
     * 上传档案包到服务器
     * @param packageZip 档案压缩包
     * @param context 上下文参数
     * @return 上传结果
     */
    protected abstract  boolean uploadPackage(File packageZip, ArchivePushContext context);

    /**
     * 调用档案系统推送接口
     * @param context 上下文参数
     * @return 是否成功
     */
    protected abstract  boolean callApi(ArchivePushContext context);

    /**
     * 档案推送完成后的后续操作
     * @param context 上下文参数
     */
    protected abstract  void whenPushFinished(ArchivePushContext context);

    private static void createTempDir(String tempDir) {
        Path path = Paths.get(tempDir);
        if (Files.exists(path)) {
            FileUtil.del(tempDir);
        }
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new ArchiveException("创建临时目录失败：" + tempDir, e);
        }
    }

}
