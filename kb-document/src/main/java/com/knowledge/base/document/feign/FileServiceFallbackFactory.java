package com.knowledge.base.document.feign;

import com.knowledge.base.common.result.Result;
import com.knowledge.base.document.dto.FileUploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务 Feign 降级工厂
 *
 * <p>当 kb-file 服务不可用时返回 null，由调用方降级到本地保存</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
public class FileServiceFallbackFactory implements FallbackFactory<FileServiceFeignClient> {

    @Override
    public FileServiceFeignClient create(Throwable cause) {
        log.error("文件服务 Feign 调用失败，触发降级：{}", cause.getMessage());
        return new FileServiceFeignClient() {
            @Override
            public Result<FileUploadResponse> uploadFile(MultipartFile file, String fileType,
                                                          Integer accessLevel, Long teamId) {
                log.warn("Feign 降级：文件上传回退，fileName={}", file.getOriginalFilename());
                return null;
            }

            @Override
            public Result<FileUploadResponse> convertImageUrl(String imageUrl) {
                log.warn("Feign 降级：URL 转换回退，imageUrl={}", imageUrl);
                return null;
            }
        };
    }
}
