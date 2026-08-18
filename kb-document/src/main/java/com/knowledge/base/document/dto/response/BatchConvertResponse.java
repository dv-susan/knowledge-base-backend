package com.knowledge.base.document.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 批量转换图片URL响应
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "批量转换图片URL响应")
public class BatchConvertResponse {

    @Schema(description = "成功转换的URL映射关系（原始URL -> 新URL）")
    private Map<String, String> urlMappings;

    @Schema(description = "转换失败的URL映射关系（原始URL -> 错误信息）")
    private Map<String, String> errorMappings;

    @Schema(description = "成功数量")
    private Integer successCount;

    @Schema(description = "失败数量")
    private Integer failureCount;
}
