package com.weaver.seconddev.hnweaver.integration.archive.ecode.controller;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.weaver.common.authority.annotation.WeaPermission;
import com.weaver.common.base.entity.result.WeaResult;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.constants.ArchiveResultStatus;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.domaim.dto.ArchiveRecordDto;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.domaim.param.FeedbackParam;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.service.ArchiveRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 姚礼林
 * @desc 档案系统调用OA的反馈接口
 * @date 2025/9/8
 **/
@WeaPermission(publicPermission = true)
@RestController
@RequestMapping({"/papi/secondev/hnweaver/integration/archive"})
@Slf4j
@RequiredArgsConstructor
public class ArchiveFeedbackController {
    private final ArchiveRecordService archiveRecordService;

    @PostMapping("/feedback")
    public WeaResult<String> feedback(@RequestBody FeedbackParam param) {
        log.info("接收参数：{}", JSON.toJSONString(param));
        if (CharSequenceUtil.isBlank(param.getProcessId())) {
            return WeaResult.fail(404, "ProcessID 不能为空");
        }
        if (param.getResult() == null) {
            return WeaResult.fail(404, "Result 不能为空");
        }

        if (!archiveRecordService.existRecord(param.getProcessId())) {
            return WeaResult.fail(404, "ProcessID 不存在，ProcessID:" + param.getProcessId());
        }

        ArchiveRecordDto recordDto = new ArchiveRecordDto();
        recordDto.setRequestId(Long.valueOf(param.getProcessId()));
        recordDto.setStatus(ArchiveResultStatus.getByValue(param.getResult()));
        recordDto.setFeedbackMsg(param.getMsg());

        boolean result = archiveRecordService.updateOrInsertRecord(recordDto, false);
        if (!result) {
            return WeaResult.success("数据更新失败");
        }
        return WeaResult.success("反馈成功");
    }
}
