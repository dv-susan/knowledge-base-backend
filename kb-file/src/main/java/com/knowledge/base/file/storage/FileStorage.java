package com.knowledge.base.file.storage;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件存储抽象接口
 * 
 * <p>定义文件存储的标准操作，支持多种存储后端（本地文件系统、云存储等）</p>
 * 
 * @author 苏三
 * @since 1.0.0
 */
public interface FileStorage {

    /**
     * 上传文件
     *
     * @param inputStream 文件输入流
     * @param relativePath 相对存储路径
     * @param fileSize 文件大小
     * @return 是否成功
     */
    boolean upload(InputStream inputStream, String relativePath, long fileSize);

    /**
     * 下载文件
     *
     * @param relativePath 相对存储路径
     * @param outputStream 输出流
     * @return 文件大小
     */
    long download(String relativePath, OutputStream outputStream);

    /**
     * 获取文件输入流
     *
     * @param relativePath 相对存储路径
     * @return 文件输入流
     */
    InputStream getInputStream(String relativePath);

    /**
     * 删除文件
     *
     * @param relativePath 相对存储路径
     * @return 是否成功
     */
    boolean delete(String relativePath);

    /**
     * 文件是否存在
     *
     * @param relativePath 相对存储路径
     * @return 是否存在
     */
    boolean exists(String relativePath);

    /**
     * 获取文件大小
     *
     * @param relativePath 相对存储路径
     * @return 文件大小
     */
    long getFileSize(String relativePath);

    /**
     * 获取存储类型
     *
     * @return 存储类型标识
     */
    String getStorageType();
}