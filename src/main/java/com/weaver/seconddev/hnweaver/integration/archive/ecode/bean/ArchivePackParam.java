package com.weaver.seconddev.hnweaver.integration.archive.ecode.bean;

import cn.hutool.core.bean.BeanUtil;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.context.PackArchivePackageContext;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 姚礼林
 * @desc 档案推送打包参数
 * @date 2025/9/22
 **/
@Setter
@Getter
@ToString
public class ArchivePackParam extends PackArchivePackageContext {
    private PackageFile packageFile;

    public ArchivePackParam(ArchiveDataModel dataModel, PackageFile packageFile) {
        super(dataModel);
        this.packageFile = packageFile;
    }

    public static ArchivePackParam create(PackArchivePackageContext context,PackageFile packageFile) {
        ArchivePackParam param = BeanUtil.copyProperties(context, ArchivePackParam.class);
        param.setPackageFile(packageFile);
        return param;
    }
}
