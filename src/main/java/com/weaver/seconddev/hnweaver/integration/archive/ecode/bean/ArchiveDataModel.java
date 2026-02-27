package com.weaver.seconddev.hnweaver.integration.archive.ecode.bean;

import com.alibaba.fastjson.JSONObject;
import com.weaver.intcenter.ias.core.api.hook.dto.EcodePushRequest;
import lombok.Data;
import java.util.*;

/**
 * 档案推送传入参数
 */
@Data
public class ArchiveDataModel {
    private String requestId;
    private String requestName;
    private String requestCreateDate;
    private String finishDateTime;
    private String workflowId;
    private Map<String, String> formdata;
    private String deptId;
    private String requestCreator;
    private List<FieldItem> fieldList;
    private String requestLevel;
    private List<Detail> details;
    private String secLevel;
    private String requestOverDate;
    private String currentDatetime;
    private String workflowTypename;
    private String deptName;


    /**
     * 表单字段
     */
    @Data
    public static class FieldItem {
        private String fieldLabel;
        private String fieldValue;
        private String fieldName;
        private String fieldType;
        private List<FileItem> fileList;
    }

    /**
     * 文件信息类
     */
    @Data
    public static class FileItem {
        private String ext;
        private String fileFtpDir;
        private Long size;
        private String fileName;
        private String fileId;
        private String fileFtpPath;
        private String md5;

    }

    /**
     * 明细表类
     */
    @Data
    public static class Detail {
        private String tableName;
        private List<List<DetailField>> fieldList;

    }

    /**
     * 明细字段类
     */
    @Data
    public static class DetailField {
        private String fieldLabel;
        private String fieldValue;
        private String fieldName;
        private String fieldType;

    }

    public static ArchiveDataModel from(EcodePushRequest request) {
        Map<String, Object> outDataModel = request.getOutDataModel();
        JSONObject json = new JSONObject(outDataModel);
        return json.toJavaObject(ArchiveDataModel.class);
    }
}
