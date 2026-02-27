package com.weaver.seconddev.hnweaver.integration.archive.ecode.service.impl;

import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 姚礼林
 * @desc MetadataXmlFileCreateServiceImpl测试类
 * @date 2025/8/20
 **/
class ArchiveXmlFileGeneratorImplTest {

    private MetadataXmlFileCreateService xmlService;
    private List<ArchiveMetadata> metadataList;
    private ArchiveWorkflowConfig workflowConfig;

    @BeforeEach
    void setUp() {
        xmlService = new MetadataXmlFileCreateService(new FreeMarkerXmlGenerator());

        // 准备测试数据
        metadataList = new ArrayList<>();

        // 添加普通元数据
        ArchiveMetadata titleMetadata = new ArchiveMetadata("tm", "测试文档标题");
        metadataList.add(titleMetadata);

        ArchiveMetadata authorMetadata = new ArchiveMetadata("zrz", "张三");
        metadataList.add(authorMetadata);

        // 添加文件类型元数据
        List<MetadataFileInfo> fileInfoList = new ArrayList<>();
        MetadataFileInfo mainFile = new MetadataFileInfo();
        mainFile.setFileName("主文档.doc");
        mainFile.setFileType("doc");
        mainFile.setFileCategoryName("正文");
        fileInfoList.add(mainFile);

        MetadataFileInfo attachmentFile = new MetadataFileInfo();
        attachmentFile.setFileName("附件.pdf");
        attachmentFile.setFileType("pdf");
        attachmentFile.setFileCategoryName("附件");
        fileInfoList.add(attachmentFile);

        ArchiveMetadata fileMetadata = ArchiveMetadata.createFileMetadata("dzgwbswj", attachmentFile);
        metadataList.add(fileMetadata);

        // 准备工作流配置
        workflowConfig = new ArchiveWorkflowConfig();
        String testTemplate = createTestTemplate();
        System.out.println(testTemplate);
        ArchivePackageConfig packageConfig = new ArchivePackageConfig();
        packageConfig.setMetadataXmlTemplate(testTemplate);
        workflowConfig.setPackageConfig(packageConfig);
    }

    @Test
    void testCreateTestTemplate() {
        String testTemplate = createTestTemplate();
        System.out.println(testTemplate);
    }

    /**
     * 创建测试用的XML模板
     */
    private String createTestTemplate() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n" +
               "<E_officialdocument>\n" +
               "  <jhsbxx>\n" +
               "    <xxzyID></xxzyID>\n" +
               "    <ywlsh></ywlsh>\n" +
               "    <letterid></letterid>\n" +
               "    <ywlx></ywlx>\n" +
               "    <mj></mj>\n" +
               "    <jjcd></jjcd>\n" +
               "    <qqf></qqf>\n" +
               "    <fkf></fkf>\n" +
               "    <jhzt></jhzt>\n" +
               "    <fkyqbsf type=\"1\"></fkyqbsf>\n" +
               "    <fksjyq></fksjyq>\n" +
               "    <aqxxx></aqxxx>\n" +
               "    <gzr></gzr>\n" +
               "    <requestName></requestName>\n" +
               "    <ecdw></ecdw>\n" +
               "    <wh></wh>\n" +
               "    <jhdx>\n" +
               "      <fsf></fsf>\n" +
               "      <jsf></jsf>\n" +
               "      <fssj></fssj>\n" +
               "    </jhdx>\n" +
               "  </jhsbxx>\n" +
               "  <jhztxx>\n" +
               "    <dzgw type=\"0\">\n" +
               "      <#-- 处理普通元数据 -->\n" +
               "      <#if simpleMetadataList?? && simpleMetadataList?size gt 0>\n" +
               "        <#list simpleMetadataList as metadata>\n" +
               "      <${metadata.name}>${metadata.value!''}</${metadata.name}>\n" +
               "        </#list>\n" +
               "      </#if>\n" +
               "      <#-- 处理文件类型元数据 -->\n" +
               "      <#if fileMetadataList?? && fileMetadataList?size gt 0>\n" +
               "        <#list fileMetadataList as metadata>\n" +
               "          <#if metadata.fileInfoList?? && metadata.fileInfoList?size gt 0>\n" +
               "            <#list metadata.fileInfoList as fileInfo>\n" +
               "      <${metadata.name}>\n" +
               "        <bswjwjm>${fileInfo.fileName!''}</bswjwjm>\n" +
               "        <bswjgssm>${fileInfo.fileType!''}</bswjgssm>\n" +
               "        <bswjwjt>${fileInfo.category.value!''}</bswjwjt>\n" +
               "      </${metadata.name}>\n" +
               "            </#list>\n" +
               "          </#if>\n" +
               "        </#list>\n" +
               "      </#if>\n" +
               "    </dzgw>\n" +
               "  </jhztxx>\n" +
               "</E_officialdocument>";
    }

    @Test
    void testCreateXml(@TempDir Path tempDir) throws IOException {
        ArchiveDataModel dataModel = new ArchiveDataModel();
        ArchiveXmlFileGenerateParam param = new ArchiveXmlFileGenerateParam(dataModel);
        // 调用服务方法
        File xmlFile = xmlService.createXml(param);

        // 验证文件是否创建成功
        assertNotNull(xmlFile);
        assertTrue(xmlFile.exists());
        assertEquals("dzgw.xml", xmlFile.getName());

        // 读取文件内容进行验证
        String xmlContent = new String(java.nio.file.Files.readAllBytes(xmlFile.toPath()), java.nio.charset.StandardCharsets.UTF_8);

        // 验证XML结构
        assertTrue(xmlContent.contains("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertTrue(xmlContent.contains("<E_officialdocument>"));
        assertTrue(xmlContent.contains("</E_officialdocument>"));

        // 验证普通元数据
        assertTrue(xmlContent.contains("<tm>测试文档标题</tm>"));
        assertTrue(xmlContent.contains("<zrz>张三</zrz>"));

        // 验证文件元数据
        assertTrue(xmlContent.contains("<dzgwbswj>"));
        assertTrue(xmlContent.contains("<bswjwjm>主文档.doc</bswjwjm>"));
        assertTrue(xmlContent.contains("<bswjgssm>doc</bswjgssm>"));
        assertTrue(xmlContent.contains("<bswjwjt>0</bswjwjt>"));
        assertTrue(xmlContent.contains("<bswjwjm>附件.pdf</bswjwjm>"));
        assertTrue(xmlContent.contains("<bswjgssm>pdf</bswjgssm>"));
        assertTrue(xmlContent.contains("<bswjwjt>1</bswjwjt>"));

        System.out.println("生成的XML内容：");
        System.out.println(xmlContent);
    }

    @Test
    void testCreateXmlWithEmptyTemplate() {
        // 测试空模板的情况
        ArchivePackageConfig packageConfig = new ArchivePackageConfig();
        ArchiveWorkflowConfig emptyConfig = new ArchiveWorkflowConfig();
        packageConfig.setMetadataXmlTemplate("");
        ArchiveXmlFileGenerateParam param = new ArchiveXmlFileGenerateParam(new ArchiveDataModel());
        param.setWorkflowConfig(emptyConfig);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            xmlService.createXml(param);
        });

        assertTrue(exception.getMessage().contains("工作流配置中的XML模板不能为空"));
    }

    @Test
    void testCreateXmlWithNullTemplate() {
        // 测试null模板的情况
        ArchiveWorkflowConfig nullConfig = new ArchiveWorkflowConfig();
        ArchivePackageConfig packageConfig = new ArchivePackageConfig();
        packageConfig.setMetadataXmlTemplate(null);
        ArchiveXmlFileGenerateParam param = new ArchiveXmlFileGenerateParam(new ArchiveDataModel());
        param.setWorkflowConfig(nullConfig);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            xmlService.createXml(param);
        });

        assertTrue(exception.getMessage().contains("工作流配置中的XML模板不能为空"));
    }
}
