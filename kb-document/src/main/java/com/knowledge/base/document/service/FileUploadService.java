package com.knowledge.base.document.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口
 *
 * <p>支持rustfs文件服务器的文件上传</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface FileUploadService {

    /**
     * 上传文件到rustfs服务器
     *
     * @param file 文件
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file);

    /**
     * 上传字节数组到rustfs服务器
     *
     * @param bytes      文件字节数组
     * @param fileName   文件名
     * @param contentType 内容类型
     * @return 文件访问URL
     */
    String uploadBytes(byte[] bytes, String fileName, String contentType);

    /**
     * 从URL下载图片并上传到rustfs
     *
     * @param imageUrl 原始图片URL
     * @return 新的图片访问URL
     */
    String uploadImageFromUrl(String imageUrl);

    /**
     * 判断是否为外部图片URL
     *
     * @param url URL地址
     * @return 是否为外部URL
     */
    boolean isExternalImageUrl(String url);
}
