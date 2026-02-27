package com.weaver.seconddev.hnweaver.integration.archive.ecode.domaim.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author 姚礼林
 * @desc 反馈接口接收参数
 * @date 2025/9/8
 **/
@Data
public class FeedbackParam {
    @JsonProperty("ProcessID")
    private String processId;
    @JsonProperty("Result")
    private Integer result;
    @JsonProperty("Msg")
    private String msg;
}
