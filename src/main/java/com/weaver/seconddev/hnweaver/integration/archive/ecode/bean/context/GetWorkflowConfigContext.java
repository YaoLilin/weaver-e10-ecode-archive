package com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.context;

import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveDataModel;
import lombok.Data;

/**
 * @author 姚礼林
 * @desc 用于创建流程档案推送配置的上下文
 * @date 2025/9/22
 **/
@Data
public class GetWorkflowConfigContext {
    private ArchiveDataModel dataModel;

    public GetWorkflowConfigContext(ArchiveDataModel dataModel) {
        this.dataModel = dataModel;
    }
}
