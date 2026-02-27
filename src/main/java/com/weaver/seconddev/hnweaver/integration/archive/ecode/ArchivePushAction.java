package com.weaver.seconddev.hnweaver.integration.archive.ecode;

import com.alibaba.fastjson.JSON;
import com.weaver.common.base.entity.result.WeaResult;
import com.weaver.common.base.enumeration.result.WeaResultCodeEnum;
import com.weaver.intcenter.ias.core.api.hook.EcodeInterface;
import com.weaver.intcenter.ias.core.api.hook.dto.EcodePushRequest;
import com.weaver.seconddev.hnweaver.common.bean.ResultAndMsg;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchiveDataModel;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.bean.ArchivePushResult;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.constants.ArchiveResultStatus;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.domaim.dto.ArchiveRecordDto;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.service.ArchiveRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 姚礼林
 * @desc 档案推送入口，需配置到档案集成ecode档案推送方案中，并修改xml配置文件
 * @date 2025/7/24
 **/
@Slf4j
@Service("ArchivePushActionByEcode")
@RequiredArgsConstructor
public class ArchivePushAction implements EcodeInterface {
    private final AbstractEcodeArchivePushManage ecodeArchivePushManage;
    private final ArchiveRecordService recordService;

    @Override
    public WeaResult<Map<String, Object>> doPush(EcodePushRequest request) {
        log.info("参数：{}", JSON.toJSONString(request));
        ArchivePushResult result = ecodeArchivePushManage.doPush(ArchiveDataModel.from(request));
        if (!result.isSuccess()) {
            log.error("档案推送错误，错误信息：{}", result.getMsg());
            recordError(Long.parseLong(request.getRequestId()), result.getMsg());
            return WeaResult.fail("档案推送错误：" + result.getMsg());
        }
        log.info("档案推送成功");
        return WeaResult.success(new HashMap<>(1));
    }

    private void recordError(long requestId, String errorMsg) {
        ArchiveRecordDto recordDto = new ArchiveRecordDto();
        recordDto.setRequestId(requestId);
        recordDto.setWorkflow(requestId);
        recordDto.setMsg(errorMsg);
        recordDto.setStatus(ArchiveResultStatus.INNER_ERROR);
        boolean result = recordService.updateOrInsertRecord(recordDto, true);
        if (!result) {
            log.error("推送错误记录到建模中失败");
        }
    }
}
