package com.weaver.seconddev.hnweaver.integration.archive.ecode.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.exception.CreateXmlException;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.service.XmlGenerator;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

/**
 * @author 姚礼林
 * @desc 使用 FreeMarker 根据模板生成xml
 * @date 2025/8/20
 **/
@Slf4j
@Component
public class FreeMarkerXmlGenerator implements XmlGenerator {

    @Override
    @Nullable
    public File createXml(Map<String, Object> data, String xmlTemplate, String savePath) {
        try {
            // 检查配置中的模板
            if (CharSequenceUtil.isBlank(xmlTemplate)) {
                log.error("xml模板为空");
                return null;
            }

            // 从配置中获取模板字符串并创建Template对象
            Template template = createTemplateFromString(xmlTemplate);

            // 创建XML文件
            File xmlFile = new File(savePath);

            // 使用FreeMarker处理模板并写入文件
            try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(xmlFile.toPath()),
                    StandardCharsets.UTF_8)) {
                template.process(data, writer);
            }

            return xmlFile;
        } catch (IOException | TemplateException e) {
            throw new CreateXmlException("生成XML文件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从字符串创建FreeMarker模板
     */
    private Template createTemplateFromString(String templateString) throws IOException {
        // 创建临时的FreeMarker配置，用于处理字符串模板
        Configuration tempConfig = new Configuration(Configuration.VERSION_2_3_31);
        tempConfig.setDefaultEncoding("UTF-8");
        tempConfig.setNumberFormat("0.######");
        tempConfig.setDateFormat("yyyy-MM-dd");
        tempConfig.setDateTimeFormat("yyyy-MM-dd HH:mm:ss");
        tempConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        tempConfig.setLogTemplateExceptions(false);

        // 从字符串创建模板
        return new Template("dynamicTemplate", new StringReader(templateString), tempConfig);
    }
}
