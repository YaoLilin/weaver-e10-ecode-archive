package com.weaver.seconddev.hnweaver.integration.archive.ecode.constants;

/**
 * @author 姚礼林
 * @desc 档案系统反馈归档状态
 * @date 2025/9/17
 **/
public enum ArchiveResultStatus {
    /**
     * 成功
     */
    SUCCESS(0),
    /**
     * 失败
     */
    FAILED(1),
    /**
     * 结构解析错误
     */
    STRUCTURAL_ANALYSIS_ERROR(2),
    /**
     * 文件指纹验证失败
     */
    FILE_VERIFY_FAILED(3),

    /**
     * 等待档案系统反馈
     */
    WAITING(99),
    /**
     * 内部错误，是指推送执行过程中系统内部发生错误
     */
    INNER_ERROR(100);

    ArchiveResultStatus(int value) {
        this.value = value;
    }

    private final int value;

    public int getValue() {
        return value;
    }

    public static ArchiveResultStatus getByValue(int value) {
        for (ArchiveResultStatus status : ArchiveResultStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return null;
    }
}
