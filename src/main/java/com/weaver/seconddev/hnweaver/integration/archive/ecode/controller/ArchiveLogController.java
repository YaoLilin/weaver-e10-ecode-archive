package com.weaver.seconddev.hnweaver.integration.archive.ecode.controller;

import cn.hutool.core.text.CharSequenceUtil;
import com.weaver.common.authority.annotation.WeaPermission;
import com.weaver.common.base.entity.result.WeaResult;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.service.ArchiveLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 姚礼林
 * @desc 档案归档日志接口
 * @date 2025/10/16
 **/
@WeaPermission(publicPermission = true)
@RestController
@RequestMapping({"/api/secondev/hnweaver/integration/archive/log"})
@Slf4j
@RequiredArgsConstructor
public class ArchiveLogController {
    private final ArchiveLogService archiveLogService;

    @GetMapping
    public WeaResult<List<String>> getLogIds(@RequestParam(name = "requestIds") String requestIds) {
        if (CharSequenceUtil.isBlank(requestIds)) {
            return WeaResult.fail("[requestIds] 参数不能为空");
        }
        String[] requestIdsArray = requestIds.split(",");
        List<String>  requestIdList = Arrays.stream(requestIdsArray).collect(Collectors.toList());
        List<String> logIds = archiveLogService.getLogIdsByRequestIds(requestIdList);
        return WeaResult.success(logIds);
    }

    @GetMapping("/all-failed")
    public WeaResult<List<String>> getAllFailedLogIds() {
        List<String> logIds = archiveLogService.getAllFailedLogIds();
        return WeaResult.success(logIds);
    }

}
