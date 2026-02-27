package com.weaver.seconddev.hnweaver.integration.archive.ecode.service.impl;

import com.jcraft.jsch.*;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveApiConfig;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveWorkflowConfig;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.exception.ArchiveConfigException;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.exception.ArchiveUploadException;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.service.ArchivePackageUploadService;

import cn.hutool.core.text.CharSequenceUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author 姚礼林
 * @desc 档案包上传业务类
 * @date 2025/9/1
 **/
@Service
@Slf4j
public class ArchivePackageUploadServiceImpl implements ArchivePackageUploadService {

    @Override
    public boolean upload(File packageFile, ArchiveWorkflowConfig config) {
        log.info("上传档案包路径：{}", packageFile.getAbsoluteFile());
        ArchiveApiConfig apiConfig = config.getApiConfig();
        if (!apiConfig.isUploadFtpEnable()) {
            log.info("SFTP 上传未启用，配置名称：{}", config.getConfigName());
            // 不启用视为成功
            return true;
        }

        String server = apiConfig.getFtpAddress();
        Integer port = apiConfig.getFtpPort();
        String user = apiConfig.getFtpUsername();
        String pass = apiConfig.getFtpPassword();
        String remotePath = apiConfig.getFtpFilePath();

        verifyFtpConfig(server, port, user, pass, remotePath);
        log.info("SFTP 配置信息：服务器：{}，端口：{}，用户名：{}，文件存放路径：{}", server, port, user, remotePath);

        Session session = null;
        Channel channel = null;
        ChannelSftp sftpChannel = null;

        try {
            debugConnection();
            JSch jsch = new JSch();
            session = jsch.getSession(user, server, port);
            // 设置 socket 超时
            session.setTimeout(30000);
            session.setPassword(pass);

            setSessionConfig(session);

            // 连接服务器
            session.connect();

            // 打开SFTP通道
            channel = session.openChannel("sftp");
            channel.connect();
            sftpChannel = (ChannelSftp) channel;

            // 切换到目标路径
            if (remotePath != null && !remotePath.isEmpty()) {
                log.info("切换到SFTP远程路径：{}", remotePath);
                sftpChannel.cd(remotePath);
            }

            String remoteFileName = packageFile.getName();
            return uploadFile(packageFile, sftpChannel, remoteFileName);
        } catch (JSchException | SftpException ex) {
            throw new ArchiveUploadException("SFTP 上传失败：" + ex.getMessage(), ex);
        } finally {
            if (sftpChannel != null) {
                sftpChannel.exit();
            }
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    private void debugConnection() {
        try {
            // 启用 JSch 调试
            JSch.setLogger(new com.jcraft.jsch.Logger() {
                @Override
                public boolean isEnabled(int level) {
                    return true;
                }

                @Override
                public void log(int level, String message) {
                    switch (level) {
                        case Logger.INFO:
                            log.info("JSch INFO: {}", message);
                            break;
                        case Logger.WARN:
                            log.warn("JSch WARN: {}", message);
                            break;
                        case Logger.ERROR:
                            log.error("JSch ERROR: {}", message);
                            break;
                        case Logger.FATAL:
                            log.error("JSch FATAL: {}", message);
                            break;
                        case Logger.DEBUG:
                            log.debug("JSch DEBUG: {}", message);
                            break;
                        default:
                            break;
                    }
                }
            });

        } catch (Exception e) {
            log.error("开启 JSch 日志调试失败", e);
        }
    }

    private static void setSessionConfig(Session session) {
        Properties connectConfig = new Properties();
        connectConfig.put("StrictHostKeyChecking", "no");
        connectConfig.put("PreferredAuthentications", "password,publickey");
        connectConfig.put("MaxAuthTries", "3");
        connectConfig.put("ServerAliveInterval", "60000");
        connectConfig.put("ServerAliveCountMax", "3");
        // 30秒连接超时
        connectConfig.put("ConnectTimeout", "30000");
        session.setConfig(connectConfig);
    }

    private static boolean uploadFile(File packageFile, ChannelSftp sftpChannel, String remoteFileName) {
        try (InputStream inputStream = Files.newInputStream(packageFile.toPath())) {
            sftpChannel.put(inputStream, remoteFileName);
            log.info("文件上传成功，文件名：{}", remoteFileName);
            return true;
        } catch (IOException | SftpException ex) {
            log.error("文件上传失败，文件名：{}", remoteFileName, ex);
            return false;
        }
    }

    private void verifyFtpConfig(String server, Integer port, String user, String pass, String remotePath) {
        if (CharSequenceUtil.isBlank(server)) {
            throw new ArchiveConfigException("SFTP 地址不能为空");
        }
        if (port == null) {
            throw new ArchiveConfigException("SFTP 端口不能为空");
        }
        if (CharSequenceUtil.isBlank(user)) {
            throw new ArchiveConfigException("SFTP 用户名不能为空");
        }
        if (CharSequenceUtil.isBlank(pass)) {
            throw new ArchiveConfigException("SFTP 密码不能为空");
        }
        if (CharSequenceUtil.isBlank(remotePath)) {
            throw new ArchiveConfigException("SFTP 远程路径不能为空");
        }
    }
}
