package com.knowledge.base.document.feign;

import com.knowledge.base.common.result.Result;
import com.knowledge.base.document.config.FeignMultipartSupportConfig;
import com.knowledge.base.document.dto.FileUploadResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务Feign客户端
 *
 * <p>用于调用文件服务的相关接口</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@FeignClient(
    name = "kb-file",
    url = "${kb-file.url:}",
    path = "/files",
    configuration = FeignMultipartSupportConfig.class,
    fallbackFactory = FileServiceFallbackFactory.class
)
public interface FileServiceFeignClient {

    /**
     * 上传单个文件（通过 Feign 发送到 kb-file 的 RUSTFS 存储）
     *
     * @param file        文件（MultipartFile）
     * @param fileType    文件类型
     * @param accessLevel 访问级别
     * @param teamId      团队ID
     * @return 文件信息
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<FileUploadResponse> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "fileType", required = false) String fileType,
            @RequestParam(value = "accessLevel", required = false) Integer accessLevel,
            @RequestParam(value = "teamId", required = false) Long teamId);

    /**
     * 从URL转换图片
     *
     * @param imageUrl 图片URL
     * @return 转换结果
     */
    @PostMapping("/convert-url")
    Result<FileUploadResponse> convertImageUrl(@RequestParam("imageUrl") String imageUrl);
}
