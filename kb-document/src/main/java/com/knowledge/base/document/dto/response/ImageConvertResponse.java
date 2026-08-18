package com.knowledge.base.document.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片URL转换响应
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "图片URL转换响应")
public class ImageConvertResponse {

    @Schema(description = "原始图片URL")
    private String originalUrl;

    @Schema(description = "转换后的图片URL")
    private String convertedUrl;
}
