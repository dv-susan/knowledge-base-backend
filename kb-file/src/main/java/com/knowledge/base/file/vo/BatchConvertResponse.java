package com.knowledge.base.file.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 批量转换响应
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchConvertResponse {

    /**
     * URL映射关系（原URL -> 新URL）
     */
    private Map<String, String> urlMappings;

    /**
     * 错误映射关系（原URL -> 错误信息）
     */
    private Map<String, String> errorMappings;

    /**
     * 成功数量
     */
    private Integer successCount;

    /**
     * 失败数量
     */
    private Integer failureCount;
}
