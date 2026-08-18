package com.knowledge.base.file.service;

import com.knowledge.base.file.vo.MediaMetadata;

/**
 * 媒体处理服务接口
 * 负责音视频文件的元数据提取、HLS转码、缩略图生成
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface MediaService {

    /**
     * 提取媒体文件元数据（duration/resolution/bitrate/codec）
     *
     * @param fileId 文件ID
     * @return 媒体元数据
     */
    MediaMetadata probeMediaInfo(Long fileId);

    /**
     * 将视频文件转码为HLS多码率格式（360p + 720p）
     * 异步执行，由RabbitMQ消费者调用
     *
     * @param fileId 文件ID
     * @return HLS播放列表相对路径
     */
    String transcodeToHls(Long fileId);

    /**
     * 从视频文件生成缩略图（关键帧截图）
     *
     * @param fileId 文件ID
     * @return 缩略图相对路径
     */
    String generateThumbnail(Long fileId);

    /**
     * 更新文件的转码状态
     *
     * @param fileId 文件ID
     * @param status 转码状态：PENDING/PROCESSING/DONE/FAILED
     */
    void updateTranscodeStatus(Long fileId, String status);
}
