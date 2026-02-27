package com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.context;

import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveDataModel;
import lombok.Data;

/**
 * @author 姚礼林
 * @desc 用于档案创建临时目录的上下文
 * @date 2025/9/22
 **/
@Data
public class TempPathContext{
    private ArchiveDataModel dataModel;

    public TempPathContext(ArchiveDataModel dataModel) {
        this.dataModel = dataModel;
    }
}
